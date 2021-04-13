package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.setup.PlayerData;
import com.mrnavastar.invsync.sql.SQLHandler;
import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerDataConversion {

    public static void nbtToSql(PlayerEntity player) {
        SQLHandler.connect();
        String uuid = player.getUuid().toString();
        PlayerData.playerDataTable.createRow("id", uuid);

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                PlayerData.playerDataTable.saveString(uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i)));
            }
            PlayerData.playerDataTable.saveString(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)));
            PlayerData.playerDataTable.saveInt(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                PlayerData.playerDataTable.saveString(uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i)));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                PlayerData.playerDataTable.saveString(uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)));
            }
        }

        if (ConfigManager.Sync_Xp) {
            PlayerData.playerDataTable.saveInt(uuid, "xp", player.experienceLevel);
            PlayerData.playerDataTable.saveFloat(uuid, "xpProgress", player.experienceProgress);
        }

        if (ConfigManager.Sync_Score) {
            PlayerData.playerDataTable.saveInt(uuid, "score", player.getScore());
        }

        if (ConfigManager.Sync_Health) {
            PlayerData.playerDataTable.saveFloat(uuid, "health", player.getHealth());
        }

        if (ConfigManager.Sync_Food_Level) {
            PlayerData.playerDataTable.saveString(uuid,"foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()));
        }

        PlayerData.playerDataTable.saveString(uuid, "statusEffects", player.getActiveStatusEffects().toString());

        SQLHandler.disconnect();
    }

    public static void sqlToNbt(PlayerEntity player) {
        SQLHandler.connect();
        String uuid = player.getUuid().toString();

        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                player.inventory.main.set(i, ConversionHelpers.stringToItemStack(PlayerData.playerDataTable
                        .loadString(uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i)))));
            }
            player.inventory.offHand.set(0, ConversionHelpers.stringToItemStack(PlayerData.playerDataTable
                    .loadString(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)))));

            player.inventory.selectedSlot = PlayerData.playerDataTable.loadInt(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                player.inventory.armor.set(i, ConversionHelpers.stringToItemStack(PlayerData.playerDataTable
                        .loadString(uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i)))));
            }
        }

        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                player.getEnderChestInventory().setStack(i, ConversionHelpers.stringToItemStack(PlayerData.playerDataTable
                        .loadString(uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)))));
            }
        }

        if (ConfigManager.Sync_Xp) {
            player.experienceLevel = PlayerData.playerDataTable.loadInt(uuid, "xp", player.experienceLevel);
            player.experienceProgress = PlayerData.playerDataTable.loadFloat(uuid, "xpProgress", player.experienceProgress);
        }

        if (ConfigManager.Sync_Score) {
            player.setScore(PlayerData.playerDataTable.loadInt(uuid, "score", player.getScore()));
        }

        if (ConfigManager.Sync_Health) {
            player.setHealth(PlayerData.playerDataTable.loadFloat(uuid, "health", player.getHealth()));
        }

        if (ConfigManager.Sync_Food_Level) {
            player.getHungerManager().fromTag(ConversionHelpers.stringToTag(PlayerData.playerDataTable
                    .loadString(uuid, "foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()))));
        }

        SQLHandler.disconnect();
    }
}
