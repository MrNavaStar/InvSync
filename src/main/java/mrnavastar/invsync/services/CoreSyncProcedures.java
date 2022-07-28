package mrnavastar.invsync.services;

import mrnavastar.invsync.api.SyncEvents;
import mrnavastar.invsync.interfaces.PlayerAdvancementTrackerInf;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import static mrnavastar.invsync.InvSync.settings;

public class CoreSyncProcedures {

    public static void init() {

        if (settings.SYNC_INVENTORY) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.getInventory().readNbt((NbtList) playerData.getNbt("INVENTORY"));
                player.getInventory().selectedSlot = playerData.getInt("SELECTED_SLOT");
            });
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.put("INVENTORY", player.getInventory().writeNbt(new NbtList()));
                playerData.put("SELECTED_SLOT", player.getInventory().selectedSlot);
            });
        }

        if (settings.SYNC_ENDER_CHEST) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getEnderChestInventory().readNbtList((NbtList) playerData.getNbt("ECHEST")));
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("ECHEST", player.getEnderChestInventory().toNbtList()));
        }

        if (settings.SYNC_FOOD_LEVEL) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getHungerManager().readNbt((NbtCompound) playerData.getNbt("HUNGER")));
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtCompound nbt = new NbtCompound();
                player.getHungerManager().writeNbt(nbt);
                playerData.put("HUNGER", nbt);
            });
        }

        if (settings.SYNC_HEALTH) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setHealth(playerData.getFloat("HEALTH")));
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("HEALTH", player.getHealth()));
        }

        if (settings.SYNC_SCORE) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setScore(playerData.getInt("SCORE")));
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("SCORE", player.getScore()));
        }

        if (settings.SYNC_XP_LEVEL) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.experienceLevel = playerData.getInt("XP");
                player.experienceProgress = playerData.getFloat("XP_PROGRESS");
            });
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.put("XP", player.experienceLevel);
                playerData.put("XP_PROGRESS", player.experienceProgress);
            });
        }

        if (settings.SYNC_STATUS_EFFECTS) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                NbtList effects = (NbtList) playerData.getNbt("EFFECTS");
                if (effects != null) {
                    player.clearStatusEffects();
                    for (NbtElement effect : effects) player.addStatusEffect(StatusEffectInstance.fromNbt((NbtCompound) effect));
                }
            });
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtList effects = new NbtList();
                for (StatusEffectInstance effect : player.getStatusEffects()) effects.add(effect.writeNbt(new NbtCompound()));
                playerData.put("EFFECTS", effects);
            });
        }

        if (settings.SYNC_ADVANCEMENTS) {
            SyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> ((PlayerAdvancementTrackerInf) player.getAdvancementTracker()).writeAdvancementData(playerData.getJson("ADVANCEMENTS")));
            SyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.put("ADVANCEMENTS", ((PlayerAdvancementTrackerInf) player.getAdvancementTracker()).readAdvancementData()));
        }
    }
}