package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.setup.ConfigManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import static com.mrnavastar.invsync.setup.PlayerDataSetup.playerDataTable;

public class PlayerDataConversion {

    public static void nbtToSql(PlayerEntity player) {
        playerDataTable.startTransaction();
        String uuid = player.getUuid().toString();
        playerDataTable.createRow("id", uuid);

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                playerDataTable.set(uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i)));
            }
            playerDataTable.set(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)));
            playerDataTable.set(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                playerDataTable.set(uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i)));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                playerDataTable.set(uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)));
            }
        }

        if (ConfigManager.Sync_Xp) {
            playerDataTable.set(uuid, "xp", player.experienceLevel);
            playerDataTable.set(uuid, "xpProgress", player.experienceProgress);
        }

        if (ConfigManager.Sync_Score) {
            playerDataTable.set(uuid, "score", player.getScore());
        }

        if (ConfigManager.Sync_Health) {
            playerDataTable.set(uuid, "health", player.getHealth());
        }

        if (ConfigManager.Sync_Food_Level) {
            playerDataTable.set(uuid,"foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()));
        }

        if (ConfigManager.Sync_Status_Effects) {
            ArrayList<String> effects = new ArrayList<>();
            for (StatusEffectInstance s : player.getStatusEffects()) {
                effects.add(ConversionHelpers.effectsToString(s));
            }
            playerDataTable.set(uuid, "statusEffects", effects.toString());
        }

        playerDataTable.endTransaction();
    }

    public static void sqlToNbt(PlayerEntity player) {
        playerDataTable.startTransaction();
        String uuid = player.getUuid().toString();

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                player.inventory.main.set(i, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .get(uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i))))));
            }
            player.inventory.offHand.set(0, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                    .get(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0))))));

            player.inventory.selectedSlot = playerDataTable.get(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                player.inventory.armor.set(i, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .get(uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i))))));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                player.getEnderChestInventory().setStack(i, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .get(uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i))))));
            }
        }

        if (ConfigManager.Sync_Xp) {
            player.experienceLevel = playerDataTable.get(uuid, "xp", player.experienceLevel);
            player.experienceProgress = playerDataTable.get(uuid, "xpProgress", player.experienceProgress);
        }

        if (ConfigManager.Sync_Score) {
            player.setScore(playerDataTable.get(uuid, "score", player.getScore()));
        }

        if (ConfigManager.Sync_Health) {
            player.setHealth(playerDataTable.get(uuid, "health", player.getHealth()));
        }

        if (ConfigManager.Sync_Food_Level) {
            player.getHungerManager().fromTag(ConversionHelpers.stringToTag(playerDataTable
                    .get(uuid, "foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()))));
        }

        if (ConfigManager.Sync_Status_Effects) {
            player.clearStatusEffects();
            String str = playerDataTable.get(uuid, "statusEffects", "[]");
            if (!str.equals("[]")) {
                String[] strArr =  str.replace("[", "").replace("]", "").split(", ");
                for (String s : strArr) {
                    player.addStatusEffect(StatusEffectInstance.fromTag(ConversionHelpers.stringToTag(s)));
                }
            }
        }

        playerDataTable.endTransaction();
    }
}