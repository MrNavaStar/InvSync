package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.column.PlayerDataColumns;
import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;

public class NBTtoSQL {

    private static final String tableName = ConfigManager.Database_Table_Name;
    private static final ArrayList<String> columnsTotal = PlayerDataColumns.columnsTotal;

    private static void convertInventory(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                SQLHandler.saveString(tableName, columnsTotal, uuid, "inv" + i, ConversionHelpers.itemStackToString(player.inventory.main.get(i)));
            }
            SQLHandler.saveString(tableName, columnsTotal, uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)));
        }
        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                SQLHandler.saveString(tableName, columnsTotal, uuid, "armour" + i, ConversionHelpers.itemStackToString(player.inventory.armor.get(i)));
            }
        }
        SQLHandler.saveInt(tableName, columnsTotal, uuid, "selectedSlot", player.inventory.selectedSlot);
    }

    private static void convertEnderChest(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                SQLHandler.saveString(tableName, columnsTotal, uuid, "eChest" + i, ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)));
            }
        }
    }

    private static void convertStats(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_Xp) {
            SQLHandler.saveInt(tableName, columnsTotal, uuid, "xp", player.experienceLevel);
            SQLHandler.saveFloat(tableName, columnsTotal, uuid, "xpProgress", player.experienceProgress);
        }
        if (ConfigManager.Sync_Score) {
            SQLHandler.saveInt(tableName, columnsTotal, uuid, "score", player.getScore());
        }
        if (ConfigManager.Sync_Health) {
            SQLHandler.saveFloat(tableName, columnsTotal, uuid, "health", player.getHealth());
        }
        if (ConfigManager.Sync_Food_Level) {
            SQLHandler.saveString(tableName, columnsTotal, uuid,"foodLevel", ConversionHelpers.foodLevelToString(player.getHungerManager()));
        }
    }

    private static void convertStatusEffects(PlayerEntity player, String uuid) {
        SQLHandler.saveString(tableName, columnsTotal, uuid, "statusEffects", player.getActiveStatusEffects().toString());
    }

    public static void convert(PlayerEntity player) {
        SQLHandler.connect();
        String uuid = player.getUuid().toString();
        SQLHandler.createRow("uuid", uuid, tableName);

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);
        convertStatusEffects(player, uuid);

        SQLHandler.disconnect();
    }
}