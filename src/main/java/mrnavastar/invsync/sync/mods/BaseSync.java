package mrnavastar.invsync.sync.mods;

import mrnavastar.invsync.InvSync;
import mrnavastar.invsync.interfaces.IPlayerAdvancementTracker;
import mrnavastar.invsync.interfaces.IServerStatHandler;
import mrnavastar.invsync.sync.SyncEvents;
import mrnavastar.invsync.sync.SyncManager;
import mrnavastar.sqlib.Table;
import mrnavastar.sqlib.database.Database;
import mrnavastar.sqlib.sql.SQLDataType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

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
            SyncEvents.LOAD_PLAYER_DATA.register("base", ((player, data) -> {
                NbtCompound currentNbt = new NbtCompound();
                NbtCompound newNbt = (NbtCompound) data.getNbt("playerData");
                player.writeNbt(currentNbt);

                InvSync.playerDataBlacklist.forEach(tag -> {
                    NbtElement nbt = currentNbt.get(tag);
                    if (nbt != null) newNbt.put(tag, nbt);
                });

                player.readNbt(newNbt);
            }));
            SyncEvents.SAVE_PLAYER_DATA.register("base", (player, data) -> {
                NbtCompound nbt = new NbtCompound();
                player.writeNbt(nbt);
                InvSync.playerDataBlacklist.forEach(nbt::remove);
                data.put("playerData", nbt);
            });
        }

        if (InvSync.config.SYNC_ADVANCEMENTS) {
            SyncEvents.LOAD_PLAYER_DATA.register("base", ((player, data) -> ((IPlayerAdvancementTracker) player.getAdvancementTracker()).writeAdvancementData(data.getJson("advancements"))));
            SyncEvents.SAVE_PLAYER_DATA.register("base", (((player, data) -> data.put("advancements", ((IPlayerAdvancementTracker) player.getAdvancementTracker()).readAdvancementData()))));
        }

        if (InvSync.config.SYNC_STATS) {
            SyncEvents.LOAD_PLAYER_DATA.register("base", (player, data) -> ((IServerStatHandler) player.getStatHandler()).writeStatData(data.getString("stats")));
            SyncEvents.SAVE_PLAYER_DATA.register("base", ((player, data) -> data.put("stats", ((IServerStatHandler) player.getStatHandler()).readStatData())));
        }
    }
}