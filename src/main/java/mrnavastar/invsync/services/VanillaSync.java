package mrnavastar.invsync.services;

import mrnavastar.invsync.InvSync;
import mrnavastar.invsync.api.ServerSyncEvents;
import mrnavastar.invsync.interfaces.PlayerAdvancementTrackerInf;
import net.minecraft.nbt.NbtCompound;

import static mrnavastar.invsync.InvSync.settings;

public class VanillaSync {

    public static void init() {
        if (settings.SYNC_PLAYER_DATA) {
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtCompound nbt = new NbtCompound();
                player.writeNbt(nbt);
                for (String tag : InvSync.playerDataBlacklist) nbt.remove(tag);
                playerData.put("invsync:playerdata", nbt);
            });
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.readNbt((NbtCompound) playerData.getNbt("invsync:playerdata")));
        }
        if (settings.SYNC_ADVANCEMENTS) {
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("ADVANCEMENTS", ((PlayerAdvancementTrackerInf) player.getAdvancementTracker()).readAdvancementData()));
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> ((PlayerAdvancementTrackerInf) player.getAdvancementTracker()).writeAdvancementData(playerData.getJson("invsync:advancements")));
        }
    }
}