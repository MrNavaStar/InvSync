package mrnavastar.invsync.util;

import mrnavastar.sqlib.api.DataContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

import static mrnavastar.invsync.Invsync.playerData;
import static mrnavastar.invsync.Invsync.settings;

public class Converter {

    public static void updatePlayerData(PlayerEntity player) {
        if ((settings.SYNC_CREATIVE_MODE) || (!settings.SYNC_CREATIVE_MODE && !player.isCreative())) {
            DataContainer playerDataContainer = playerData.get(player.getUuidAsString());

            if (playerDataContainer != null) {
                    NbtCompound data = playerDataContainer.getNbtCompound("NBT_DATA");

                    if (settings.SYNC_INVENTORY) {
                        player.getInventory().readNbt((NbtList) data.get("INVENTORY"));
                        player.getInventory().selectedSlot = playerDataContainer.getInt("SELECTED_SLOT");
                    }

                    if (settings.SYNC_ENDER_CHEST)
                        player.getEnderChestInventory().readNbtList((NbtList) data.get("ECHEST"));
                    if (settings.SYNC_FOOD_LEVEL) player.getHungerManager().readNbt(data.getCompound("HUNGER"));
                    if (settings.SYNC_HEALTH) player.setHealth(playerDataContainer.getFloat("HEALTH"));
                    if (settings.SYNC_SCORE) player.setScore(playerDataContainer.getInt("SCORE"));

                    if (settings.SYNC_XP_LEVEL) {
                        player.experienceLevel = playerDataContainer.getInt("XP");
                        player.experienceProgress = playerDataContainer.getFloat("XP_PROGRESS");
                    }

                    if (settings.SYNC_STATUS_EFFECTS) {
                        NbtList effects = (NbtList) data.get("EFFECTS");
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
            DataContainer playerDataContainer = playerData.get(player.getUuidAsString());
            if (playerDataContainer == null) {
                playerDataContainer = new DataContainer(player.getUuidAsString());
                playerData.put(playerDataContainer);
            }

            NbtCompound data = new NbtCompound();

            if (settings.SYNC_INVENTORY) {
                NbtList inventory = new NbtList();
                player.getInventory().writeNbt(inventory);
                data.put("INVENTORY", inventory);
                playerDataContainer.put("SELECTED_SLOT", player.getInventory().selectedSlot);
            }

            if (settings.SYNC_ENDER_CHEST) data.put("ECHEST", player.getEnderChestInventory().toNbtList());

            if (settings.SYNC_FOOD_LEVEL) {
                NbtCompound hunger = new NbtCompound();
                player.getHungerManager().writeNbt(hunger);
                data.put("HUNGER", hunger);
            }

            if (settings.SYNC_STATUS_EFFECTS) {
                NbtList effects = new NbtList();
                for (StatusEffectInstance effect : player.getStatusEffects()) {
                    NbtCompound nbt = new NbtCompound();
                    effect.writeNbt(nbt);
                    effects.add(nbt);
                }
                data.put("EFFECTS", effects);
            }

            if (!data.isEmpty()) playerDataContainer.put("NBT_DATA", data);
            if (settings.SYNC_HEALTH) playerDataContainer.put("HEALTH", player.getHealth());
            if (settings.SYNC_SCORE) playerDataContainer.put("SCORE", player.getScore());

            if (settings.SYNC_XP_LEVEL) {
                playerDataContainer.put("XP", player.experienceLevel);
                playerDataContainer.put("XP_PROGRESS", player.experienceProgress);
            }
        }
    }
}