package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.entity.player.PlayerEntity;

public class NBTtoSQL {

    private static void convertInventory(PlayerEntity player, String uuid) {
        for (int i = 0; i < 36; i++) {
            SQLHandler.saveItem(uuid, "inv" + i, player.inventory.main.get(i));
        }
        SQLHandler.saveItem(uuid, "offHand", player.inventory.offHand.get(0));

        for (int i = 0; i < 4; i++) {
            SQLHandler.saveItem(uuid, "armour" + i, player.inventory.armor.get(i));
        }
    }

    private static void convertEnderChest(PlayerEntity player, String uuid) {
        for (int i = 0; i < 27; i++) {
            SQLHandler.saveItem(uuid, "eChest" + i, player.getEnderChestInventory().getStack(i));
        }
    }

    private static void convertStats(PlayerEntity player, String uuid) {
        SQLHandler.saveInt(uuid, "xp", player.experienceLevel);
        SQLHandler.saveFloat(uuid, "xpProgress", player.experienceProgress);

        SQLHandler.saveInt(uuid, "score", player.getScore());

        SQLHandler.saveFloat(uuid, "health", player.getHealth());

        SQLHandler.saveInt(uuid, "foodLevel", player.getHungerManager().getFoodLevel());
        SQLHandler.saveFloat(uuid, "saturation", player.getHungerManager().getSaturationLevel());
    }

    public static void convert(PlayerEntity player) {
        SQLHandler.connect();
        String uuid = player.getUuid().toString();
        SQLHandler.createRow(uuid);

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);
        SQLHandler.disconnect();
    }
}

