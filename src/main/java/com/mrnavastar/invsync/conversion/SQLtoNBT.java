package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.entity.player.PlayerEntity;

public class SQLtoNBT {

    private static void convertInventory(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                player.inventory.main.set(i, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "inv" + i,
                        ConversionHelpers.itemStackToString(player.inventory.main.get(i)))));
            }
            player.inventory.offHand.set(0, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "offHand",
                    ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)))));
            player.inventory.selectedSlot = SQLHandler.loadInt(uuid, "selectedSlot", player.inventory.selectedSlot);
        }
        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                player.inventory.armor.set(i, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "armour" + i,
                        ConversionHelpers.itemStackToString(player.inventory.armor.get(i)))));
            }
        }
    }

    private static void convertEnderChest(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                player.getEnderChestInventory().setStack(i, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "eChest" + i,
                        ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)))));
            }
        }
    }

    private static void convertStats(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_Xp) {
            player.experienceLevel = SQLHandler.loadInt(uuid, "xp", player.experienceLevel);
            player.experienceProgress = SQLHandler.loadFloat(uuid, "xpProgress", player.experienceProgress);
        }
        if (ConfigManager.Sync_Score) {
            player.setScore(SQLHandler.loadInt(uuid, "score", player.getScore()));
        }
        if (ConfigManager.Sync_Health) {
            player.setHealth(SQLHandler.loadFloat(uuid, "health", player.getHealth()));
        }
        if (ConfigManager.Sync_Food_Level) {
            player.getHungerManager().fromTag(ConversionHelpers.stringToTag(SQLHandler.loadString(uuid, "foodLevel",
                    ConversionHelpers.foodLevelToString(player.getHungerManager()))));
        }
    }

    private static void convertStatusEffects(PlayerEntity player, String uuid) {
        //StatusEffectInstance instance = new S
        //player.addStatusEffect(new StatusEffectInstance(player.))
    }

    public static void convert(PlayerEntity player) {
        SQLHandler.connect();
        String uuid = player.getUuid().toString();

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);

        SQLHandler.disconnect();
    }
}