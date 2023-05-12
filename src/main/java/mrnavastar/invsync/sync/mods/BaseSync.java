package mrnavastar.invsync.sync.mods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrnavastar.invsync.InvSync;
import mrnavastar.invsync.interfaces.IServerStatHandler;
import mrnavastar.invsync.sync.SyncEvents;
import mrnavastar.invsync.sync.SyncManager;
import mrnavastar.invsync.util.BlacklistParser;
import mrnavastar.sqlib.Table;
import mrnavastar.sqlib.database.Database;
import mrnavastar.sqlib.sql.SQLDataType;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BaseSync {

    public static void init(Database database) {
        Table baseData = database.createTable("base")
                .addColumn("playerData", SQLDataType.NBT)
                .addColumn("advancements", SQLDataType.JSON)
                .addColumn("stats", SQLDataType.STRING)
                .addColumn("dataInUse", SQLDataType.BOOL)
                .finish();

        SyncManager.registerMod("base", baseData);

        if (InvSync.config.SYNC_PLAYER_DATA) {
            ArrayList<String> playerDataBlacklist = BlacklistParser.getPlayerDataBlacklist();

            SyncEvents.LOAD_PLAYER_DATA.register("base", ((player, data) -> {
                NbtCompound currentNbt = new NbtCompound();
                NbtCompound newNbt = (NbtCompound) data.getNbt("playerData");
                player.writeNbt(currentNbt);

                playerDataBlacklist.forEach(tag -> {
                    NbtElement nbt = currentNbt.get(tag);
                    if (nbt != null) newNbt.put(tag, nbt);
                });

                player.readNbt(newNbt);
            }));

            SyncEvents.SAVE_PLAYER_DATA.register("base", (player, data) -> {
                NbtCompound nbt = new NbtCompound();
                player.writeNbt(nbt);
                playerDataBlacklist.forEach(nbt::remove);
                data.put("playerData", nbt);
            });
        }

        if (InvSync.config.SYNC_ADVANCEMENTS) {
            AdvancementProgress.Serializer serializer = new AdvancementProgress.Serializer();

            SyncEvents.LOAD_PLAYER_DATA.register("base", (player, data) -> {
                try {
                    JsonElement json = data.getJson("advancements");
                    File advancementFile = new File("world/" + WorldSavePath.ADVANCEMENTS.getRelativePath() + "/" + player.getUuid() + ".json");
                    advancementFile.mkdir();
                    FileWriter writer = new FileWriter(advancementFile);
                    writer.write(json.toString());
                    writer.close();
                    player.getAdvancementTracker().reload(InvSync.advancementLoader);
                } catch (IOException ignore) {}
            });

            SyncEvents.SAVE_PLAYER_DATA.register("base", ((player, data) -> {
                PlayerAdvancementTracker advancementTracker = player.getAdvancementTracker();
                JsonObject json = new JsonObject();

                for (Advancement advancement : InvSync.advancementLoader.getAdvancements()) {
                    AdvancementProgress progress = advancementTracker.getProgress(advancement);
                    if (progress.isAnyObtained()) json.add(advancement.getId().toString(), serializer.serialize(progress, null, null));
                }

                data.put("advancements", json);
            }));
        }

        if (InvSync.config.SYNC_STATS) {
            SyncEvents.LOAD_PLAYER_DATA.register("base", (player, data) -> ((IServerStatHandler) player.getStatHandler()).writeStatData(data.getString("stats")));
            SyncEvents.SAVE_PLAYER_DATA.register("base", ((player, data) -> data.put("stats", ((IServerStatHandler) player.getStatHandler()).readStatData())));
        }
    }
}