package mrnavastar.invsync.services;

import mrnavastar.invsync.api.ServerSyncEvents;
import mrnavastar.invsync.interfaces.PlayerAdvancementTrackerInf;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import static mrnavastar.invsync.InvSync.settings;

public class CoreSyncProcedures {

    public static void init() {
        if (settings.SYNC_INVENTORY) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.getInventory().readNbt((NbtList) playerData.getNbt("INVENTORY"));
                player.getInventory().selectedSlot = playerData.getInt("SELECTED_SLOT");
            });
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.put("INVENTORY", player.getInventory().writeNbt(new NbtList()));
                playerData.put("SELECTED_SLOT", player.getInventory().selectedSlot);
            });
        }

        if (settings.SYNC_ENDER_CHEST) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getEnderChestInventory().readNbtList((NbtList) playerData.getNbt("ECHEST")));
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("ECHEST", player.getEnderChestInventory().toNbtList()));
        }

        if (settings.SYNC_FOOD_LEVEL) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getHungerManager().readNbt((NbtCompound) playerData.getNbt("HUNGER")));
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtCompound nbt = new NbtCompound();
                player.getHungerManager().writeNbt(nbt);
                playerData.put("HUNGER", nbt);
            });
        }

        if (settings.SYNC_HEALTH) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setHealth(playerData.getFloat("HEALTH")));
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("HEALTH", player.getHealth()));
        }

        if (settings.SYNC_SCORE) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setScore(playerData.getInt("SCORE")));
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("SCORE", player.getScore()));
        }

        if (settings.SYNC_XP_LEVEL) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.experienceLevel = playerData.getInt("XP");
                player.experienceProgress = playerData.getFloat("XP_PROGRESS");
            });
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.put("XP", player.experienceLevel);
                playerData.put("XP_PROGRESS", player.experienceProgress);
            });
        }

        if (settings.SYNC_STATUS_EFFECTS) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                NbtList effects = (NbtList) playerData.getNbt("EFFECTS");
                if (effects != null) {
                    player.clearStatusEffects();
                    for (NbtElement effect : effects) player.addStatusEffect(StatusEffectInstance.fromNbt((NbtCompound) effect));
                }
            });
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtList effects = new NbtList();
                for (StatusEffectInstance effect : player.getStatusEffects()) effects.add(effect.writeNbt(new NbtCompound()));
                playerData.put("EFFECTS", effects);
            });
        }

        if (settings.SYNC_ADVANCEMENTS) {
            ServerSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> ((PlayerAdvancementTrackerInf) player.getAdvancementTracker()).writeAdvancementData(playerData.getJson("ADVANCEMENTS")));
            ServerSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("ADVANCEMENTS", ((PlayerAdvancementTrackerInf) player.getAdvancementTracker()).readAdvancementData()));
        }
    }
}