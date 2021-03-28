package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.entity.player.PlayerEntity;

public class SQLtoNBT {

    private static void convertInventory(PlayerEntity player, String uuid) {
        for (int i = 0; i < 36; i++) {
            player.inventory.main.set(i, SQLHandler.loadItem(uuid, "inv" + i));
        }
        player.inventory.offHand.set(0, SQLHandler.loadItem(uuid, "offHand"));

        for (int i = 0; i < 4; i++) {
            player.inventory.armor.set(i, SQLHandler.loadItem(uuid, "armour" + i));
        }
    }

    private static void convertEnderChest(PlayerEntity player, String uuid) {
        for (int i = 0; i < 27; i++) {
            player.getEnderChestInventory().setStack(i, SQLHandler.loadItem(uuid, "eChest" + i));
        }
    }

    private static void convertStats(PlayerEntity player, String uuid) {

        player.experienceLevel = SQLHandler.loadInt(uuid, "xp", 0);
        player.experienceProgress = SQLHandler.loadFloat(uuid, "xpProgress", 0);

        player.setScore(SQLHandler.loadInt(uuid, "score", 0));

        player.setHealth(SQLHandler.loadFloat(uuid, "health", 20));

        player.getHungerManager().setFoodLevel(SQLHandler.loadInt(uuid, "foodLevel", 20));
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

