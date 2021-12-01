package mrnavastar.invsync.util;

import mrnavastar.sqlib.api.DataContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import static mrnavastar.invsync.InvSync.playerData;
import static mrnavastar.invsync.InvSync.settings;

public class Converter {

    public static void updatePlayerData(PlayerEntity player) {
        if (settings.SYNC_CREATIVE_MODE || !player.isCreative()) {
            DataContainer playerDataContainer = playerData.get(player.getUuid());

            if (playerDataContainer != null) {
                    if (settings.SYNC_INVENTORY) {
                        player.getInventory().readNbt((NbtList) playerDataContainer.getNbt("INVENTORY"));
                        player.getInventory().selectedSlot = playerDataContainer.getInt("SELECTED_SLOT");
                    }

                    if (settings.SYNC_ENDER_CHEST) player.getEnderChestInventory().readNbtList((NbtList) playerDataContainer.getNbt("ECHEST"));
                    if (settings.SYNC_FOOD_LEVEL) player.getHungerManager().readNbt((NbtCompound) playerDataContainer.getNbt("HUNGER"));
                    if (settings.SYNC_HEALTH) player.setHealth(playerDataContainer.getFloat("HEALTH"));
                    if (settings.SYNC_SCORE) player.setScore(playerDataContainer.getInt("SCORE"));

                    if (settings.SYNC_XP_LEVEL) {
                        player.experienceLevel = playerDataContainer.getInt("XP");
                        player.experienceProgress = playerDataContainer.getFloat("XP_PROGRESS");
                    }

                    if (settings.SYNC_STATUS_EFFECTS) {
                        NbtList effects = (NbtList) playerDataContainer.getNbt("EFFECTS");
                        if (effects != null) {
                            player.clearStatusEffects();
                            for (NbtElement effect : effects) {
                                player.addStatusEffect(StatusEffectInstance.fromNbt((NbtCompound) effect));
                            }
                        }
                    }
            }
        }
    }

    public static void savePlayerData(PlayerEntity player) {
        if ((settings.SYNC_CREATIVE_MODE) || (!settings.SYNC_CREATIVE_MODE && !player.isCreative())) {
            DataContainer playerDataContainer = playerData.get(player.getUuid());
            if (playerDataContainer == null) {
                playerDataContainer = playerData.createDataContainer(player.getUuid());
                playerData.put(playerDataContainer);
            }

            if (settings.SYNC_INVENTORY) {
                playerDataContainer.put("INVENTORY", player.getInventory().writeNbt(new NbtList()));
                playerDataContainer.put("SELECTED_SLOT", player.getInventory().selectedSlot);
            }

            if (settings.SYNC_ENDER_CHEST) playerDataContainer.put("ECHEST", player.getEnderChestInventory().toNbtList());

            if (settings.SYNC_FOOD_LEVEL) {
                NbtCompound nbt = new NbtCompound();
                player.getHungerManager().writeNbt(nbt);
                playerDataContainer.put("HUNGER", nbt);
            }

            if (settings.SYNC_STATUS_EFFECTS) {
                NbtList effects = new NbtList();
                for (StatusEffectInstance effect : player.getStatusEffects()) effects.add(effect.writeNbt(new NbtCompound()));
                playerDataContainer.put("EFFECTS", effects);
            }

            if (settings.SYNC_HEALTH) playerDataContainer.put("HEALTH", player.getHealth());

            if (settings.SYNC_SCORE) playerDataContainer.put("SCORE", player.getScore());

            if (settings.SYNC_XP_LEVEL) {
                playerDataContainer.put("XP", player.experienceLevel);
                playerDataContainer.put("XP_PROGRESS", player.experienceProgress);
            }
        }
    }
}