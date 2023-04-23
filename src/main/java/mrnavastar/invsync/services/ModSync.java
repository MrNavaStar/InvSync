package mrnavastar.invsync.services;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.player.PlayerData;
import mrnavastar.invsync.InvSync;
import mrnavastar.invsync.api.ServerSyncEvents;
import net.minecraft.nbt.NbtCompound;

public class ModSync {

    public static void initCobblemon() {
        Cobblemon cobblemon = Cobblemon.INSTANCE;

        ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
            try {
                NbtCompound pc = new NbtCompound();
                cobblemon.getStorage().getPC(player.getUuid()).saveToNBT(pc);
                playerData.put("cobblemon:pc", pc);
            } catch (NoPokemonStoreException ignore) {}

            playerData.put("cobblemon:playerdata", InvSync.GSON.toJson(cobblemon.getPlayerData().get(player)));

            NbtCompound parties = new NbtCompound();
            cobblemon.getStorage().getParty(player).saveToNBT(parties);
            playerData.put("cobblemon:parties", parties);
        });
        ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
            try {
                cobblemon.getStorage().getPC(player.getUuid()).loadFromNBT((NbtCompound) playerData.getNbt("cobblemon:pc"));
            } catch (NoPokemonStoreException ignore) {}

            PlayerData cobblemonPlayerData = InvSync.GSON.fromJson(playerData.getJson("cobblemon:playerdata"), PlayerData.class);
            cobblemon.getPlayerData().saveSingle(cobblemonPlayerData); // Idk if this is gonna work.

            cobblemon.getStorage().getParty(player).loadFromNBT((NbtCompound) playerData.getNbt("cobblemon:parties"));
        });
    }
}
