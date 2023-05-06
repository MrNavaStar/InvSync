package mrnavastar.invsync.services;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.player.PlayerData;
import mrnavastar.invsync.InvSync;
import mrnavastar.invsync.interfaces.IPlayerAdvancementTracker;
import mrnavastar.invsync.interfaces.IServerStatHandler;
import mrnavastar.sqlib.Table;
import mrnavastar.sqlib.database.Database;
import mrnavastar.sqlib.sql.SQLDataType;
import net.minecraft.nbt.NbtCompound;

public class ModSync {

    public static void initBase(Database database) {
        Table baseData = database.createTable("base")
                .addColumn("playerData", SQLDataType.NBT)
                .addColumn("advancements", SQLDataType.JSON)
                .addColumn("stats", SQLDataType.STRING)
                .addColumn("dataInUse", SQLDataType.BOOL)
                .finish();

        SyncManager.registerMod("base", baseData);

        SyncEvents.LOAD_PLAYER_DATA.register("base", (player, data) -> data.put("dataInUse", true));
        SyncEvents.SAVE_PLAYER_DATA.register("base", (player, data) -> data.put("dataInUse", false));

        if (InvSync.settings.SYNC_PLAYER_DATA) {
            SyncEvents.LOAD_PLAYER_DATA.register("base", ((player, data) -> player.readNbt((NbtCompound) data.getNbt("playerData"))));
            SyncEvents.SAVE_PLAYER_DATA.register("base", (player, data) -> {
                NbtCompound nbt = new NbtCompound();
                player.writeNbt(nbt);
                for (String tag : InvSync.playerDataBlacklist) nbt.remove(tag);
                data.put("playerData", nbt);
            });
        }

        if (InvSync.settings.SYNC_ADVANCEMENTS) {
            SyncEvents.LOAD_PLAYER_DATA.register("base", ((player, data) -> ((IPlayerAdvancementTracker) player.getAdvancementTracker()).writeAdvancementData(data.getJson("advancements"))));
            SyncEvents.SAVE_PLAYER_DATA.register("base", (((player, data) -> data.put("advancements", ((IPlayerAdvancementTracker) player.getAdvancementTracker()).readAdvancementData()))));
        }

        if (InvSync.settings.SYNC_STATS) {
            SyncEvents.LOAD_PLAYER_DATA.register("base", (player, data) -> ((IServerStatHandler) player.getStatHandler()).writeStatData(data.getString("stats")));
            SyncEvents.SAVE_PLAYER_DATA.register("base", ((player, data) -> data.put("stats", ((IServerStatHandler) player.getStatHandler()).readStatData())));
        }
    }

    public static void initCobblemon(Database database) {
        Cobblemon cobblemon = Cobblemon.INSTANCE;
        Table cobblemonData = database.createTable("cobblemon")
                .addColumn("playerData", SQLDataType.JSON)
                .addColumn("pc", SQLDataType.NBT)
                .addColumn("party", SQLDataType.NBT)
                .finish();

        SyncManager.registerMod("cobblemon", cobblemonData);

        SyncEvents.SAVE_PLAYER_DATA.register("cobblemon", (player, data) -> {
            try {
                NbtCompound pc = new NbtCompound();
                cobblemon.getStorage().getPC(player.getUuid()).saveToNBT(pc);
                data.put("pc", pc);
            } catch (NoPokemonStoreException ignore) {}

            data.put("playerData", InvSync.GSON.toJson(cobblemon.getPlayerData().get(player)));

            NbtCompound party = new NbtCompound();
            cobblemon.getStorage().getParty(player).saveToNBT(party);
            data.put("party", party);
        });

        SyncEvents.LOAD_PLAYER_DATA.register("cobblemon", (player, playerData) -> {
            try {
                cobblemon.getStorage().getPC(player.getUuid()).loadFromNBT((NbtCompound) playerData.getNbt("pc"));
            } catch (NoPokemonStoreException ignore) {}

            PlayerData cobblemonPlayerData = InvSync.GSON.fromJson(playerData.getJson("playerData"), PlayerData.class);
            cobblemon.getPlayerData().saveSingle(cobblemonPlayerData); // Idk if this is gonna work.

            cobblemon.getStorage().getParty(player).loadFromNBT((NbtCompound) playerData.getNbt("party"));
        });
    }
}