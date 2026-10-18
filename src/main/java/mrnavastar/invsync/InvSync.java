package mrnavastar.invsync;

import com.google.gson.JsonParser;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InvSync implements DedicatedServerModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("InvSync");

    /** Player NBT keys that belong to this server's world (position, vehicle, spawn point...) and must never travel. */
    static final Set<String> LOCAL_TAGS = Set.of(
            // Entity
            "Pos", "Motion", "Rotation", "fall_distance", "Fire", "HasVisualFire", "OnGround", "PortalCooldown",
            "TicksFrozen", "NoGravity", "Passengers", "UUID",
            // LivingEntity
            "FallFlying", "sleeping_pos", "last_hurt_by_player", "last_hurt_by_player_memory_time", "last_hurt_by_mob",
            "ticks_since_last_hurt_by_mob", "current_explosion_impact_pos", "current_impulse_context_reset_grace_time",
            // Player
            "abilities", "SleepTimer", "LastDeathLocation",
            // ServerPlayer
            "Dimension", "RootVehicle", "ender_pearls", "ShoulderEntityLeft", "ShoulderEntityRight", "respawn",
            "warden_spawn_tracker", "entered_nether_pos", "last_explosion_impact_pos", "raid_omen_position",
            "spawn_extra_particles_on_fall", "DataVersion");

    static Config config;
    static Db db;
    /** Players whose data came from the DB (or who are new). Only these get written back. Server thread only. */
    private static final Set<UUID> synced = new HashSet<>();

    @Override
    public void onInitializeServer() {
        try {
            config = Config.load(FabricLoader.getInstance().getConfigDir().resolve("invsync.json"));
            if (config.sqlite_file.equals(new Config().sqlite_file))
                throw new IllegalStateException("Set sqlite_file in config/invsync.json to the SQLite file shared by all servers");
            db = new Db(config.sqlite_file);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("InvSync failed to start", e);
        }
        LOG.info("Using {}", config.sqlite_file);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onJoin(handler.getPlayer(), server));
        ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) ->
                server.getPlayerList().getPlayers().forEach(p -> { if (synced.contains(p.getUUID())) save(p); }));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            try { db.close(); } catch (SQLException e) { LOG.warn("Failed to close database", e); }
        });
    }

    /** Called from PlayerListMixin before vanilla saves and drops the leaving player. */
    public static void onLeave(ServerPlayer player) {
        if (!synced.remove(player.getUUID())) return;
        // A failed save must not signal a completed handoff. Timeout recovery can still load the older snapshot.
        if (save(player)) unlock(player.getUUID());
    }

    /**
     * Wait off-thread for the previous server to release the player's row, or for the timeout to expire,
     * then load on the server thread.
     */
    private static void onJoin(ServerPlayer player, MinecraftServer server) {
        UUID uuid = player.getUUID();
        Thread.startVirtualThread(() -> {
            try {
                long deadline = System.currentTimeMillis() + config.lock_timeout_seconds * 1000L;
                while (db.isLocked(uuid)) {
                    if (System.currentTimeMillis() > deadline) {
                        // Recover abandoned locks. The timeout cannot distinguish a crashed server from one that is still saving.
                        LOG.warn("{}: data still locked by another server after {}s, taking over", player.getPlainTextName(), config.lock_timeout_seconds);
                        break;
                    }
                    Thread.sleep(100);
                }
                db.setLocked(uuid, true);
            } catch (Exception e) {
                LOG.error("{}: failed to lock data, not syncing this session", player.getPlainTextName(), e);
                return;
            }
            server.execute(() -> {
                if (server.getPlayerList().getPlayer(uuid) != player) { // left while we were waiting
                    unlock(uuid);
                    return;
                }
                try {
                    Db.Row row = db.get(uuid);
                    if (row != null) load(player, server, row);
                    synced.add(uuid);
                } catch (Exception e) {
                    // Not marking as synced: we must not overwrite the DB with this server's local state.
                    LOG.error("{}: failed to load data, not syncing this session", player.getPlainTextName(), e);
                    unlock(uuid);
                }
            });
        });
    }

    private static boolean save(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        try {
            byte[] data = null;
            String advancements = null, stats = null;
            if (config.sync_player_data) {
                CompoundTag tag = toTag(player);
                LOCAL_TAGS.forEach(tag::remove);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                NbtIo.writeCompressed(tag, out);
                data = out.toByteArray();
            }
            // Use vanilla save methods to serialize advancements and stats, then read the resulting JSON files.
            if (config.sync_advancements) {
                player.getAdvancements().save();
                advancements = Files.readString(advancementsFile(server, player));
            }
            if (config.sync_stats) {
                player.getStats().save();
                stats = Files.readString(server.getWorldPath(LevelResource.PLAYER_STATS_DIR).resolve(player.getUUID() + ".json"));
            }
            db.put(player.getUUID(), new Db.Row(data, advancements, stats));
            return true;
        } catch (Exception e) {
            LOG.error("{}: failed to save data", player.getPlainTextName(), e);
            return false;
        }
    }

    private static void load(ServerPlayer player, MinecraftServer server, Db.Row row) throws IOException {
        if (config.sync_player_data && row.playerData() != null) {
            CompoundTag incoming = NbtIo.readCompressed(new ByteArrayInputStream(row.playerData()), NbtAccounter.unlimitedHeap());
            CompoundTag local = toTag(player);
            for (String key : LOCAL_TAGS) {
                incoming.remove(key);
                Tag value = local.get(key);
                if (value != null) incoming.put(key, value);
            }
            player.removeAllEffects(); // load() replaces effects silently; this tells the client to drop the old icons
            player.load(TagValueInput.create(ProblemReporter.DISCARDING, player.registryAccess(), incoming));
            server.getPlayerList().sendAllPlayerInfo(player);
            server.getPlayerList().sendActivePlayerEffects(player);
        }
        if (config.sync_advancements && row.advancements() != null) {
            Path file = advancementsFile(server, player);
            Files.createDirectories(file.getParent());
            Files.writeString(file, row.advancements());
            player.getAdvancements().reload(server.getAdvancements());
        }
        if (config.sync_stats && row.stats() != null) {
            player.getStats().parse(server.getFixerUpper(), JsonParser.parseString(row.stats()));
            player.getStats().markAllDirty();
        }
    }

    private static CompoundTag toTag(ServerPlayer player) {
        TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
        player.saveWithoutId(out);
        return out.buildResult();
    }

    private static Path advancementsFile(MinecraftServer server, ServerPlayer player) {
        return server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(player.getUUID() + ".json");
    }

    private static void unlock(UUID uuid) {
        try { db.setLocked(uuid, false); } catch (SQLException e) { LOG.error("Failed to unlock {}", uuid, e); }
    }
}
