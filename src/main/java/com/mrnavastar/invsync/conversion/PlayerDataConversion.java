package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

import static com.mrnavastar.invsync.setup.PlayerData.playerDataTable;

public class PlayerDataConversion {

    public static void nbtToSql(PlayerEntity player) {
        playerDataTable.startTransaction();
        String uuid = player.getUuid().toString();
        playerDataTable.createRow("id", uuid);

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                playerDataTable.saveString(uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i)));
            }
            playerDataTable.saveString(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)));
            playerDataTable.saveInt(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                playerDataTable.saveString(uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i)));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                playerDataTable.saveString(uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)));
            }
        }

        if (ConfigManager.Sync_Xp) {
            playerDataTable.saveInt(uuid, "xp", player.experienceLevel);
            playerDataTable.saveFloat(uuid, "xpProgress", player.experienceProgress);
        }

        if (ConfigManager.Sync_Score) {
            playerDataTable.saveInt(uuid, "score", player.getScore());
        }

        if (ConfigManager.Sync_Health) {
            playerDataTable.saveFloat(uuid, "health", player.getHealth());
        }

        if (ConfigManager.Sync_Food_Level) {
            playerDataTable.saveString(uuid,"foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()));
        }

        if (ConfigManager.Sync_Status_Effects) {
            ArrayList<String> effects = new ArrayList<>();
            for (StatusEffectInstance s : player.getStatusEffects()) {
                effects.add(ConversionHelpers.effectsToString(s));
            }
            playerDataTable.saveString(uuid, "statusEffects", effects.toString());
        }

        playerDataTable.endTransaction();
    }

    public static void sqlToNbt(PlayerEntity player) {
        playerDataTable.startTransaction();
        String uuid = player.getUuid().toString();

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                player.inventory.main.set(i, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .loadString(uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i))))));
            }
            player.inventory.offHand.set(0, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                    .loadString(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0))))));

            player.inventory.selectedSlot = playerDataTable.loadInt(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                player.inventory.armor.set(i, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .loadString(uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i))))));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                player.getEnderChestInventory().setStack(i, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .loadString(uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i))))));
            }
        }

        if (ConfigManager.Sync_Xp) {
            player.experienceLevel = playerDataTable.loadInt(uuid, "xp", player.experienceLevel);
            player.experienceProgress = playerDataTable.loadFloat(uuid, "xpProgress", player.experienceProgress);
        }

        if (ConfigManager.Sync_Score) {
            player.setScore(playerDataTable.loadInt(uuid, "score", player.getScore()));
        }

        if (ConfigManager.Sync_Health) {
            player.setHealth(playerDataTable.loadFloat(uuid, "health", player.getHealth()));
        }

        if (ConfigManager.Sync_Food_Level) {
            player.getHungerManager().fromTag(ConversionHelpers.stringToTag(playerDataTable
                    .loadString(uuid, "foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()))));
        }

        if (ConfigManager.Sync_Status_Effects) {
            player.clearStatusEffects();
            String str = playerDataTable.loadString(uuid, "statusEffects", "[]");
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
