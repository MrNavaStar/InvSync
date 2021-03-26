package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.entity.player.PlayerEntity;

public class SQLtoNBT {

    private static void convertInventory(PlayerEntity player, String uuid) {
        for (int i = 0; i < 36; i++) {
            player.inventory.main.set(i, SQLHandler.loadItem(uuid, "inv" + i));
        }
        player.inventory.main.set(0, SQLHandler.loadItem(uuid, "offHand"));

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

        player.experienceLevel = SQLHandler.loadInt(uuid, "xp");
        player.experienceProgress = SQLHandler.loadFloat(uuid, "xpProgress");

        player.setScore(SQLHandler.loadInt(uuid, "score"));

        player.setHealth(SQLHandler.loadFloat(uuid, "health"));

        player.getHungerManager().setFoodLevel(SQLHandler.loadInt(uuid, "foodLevel"));
        player.getHungerManager().setSaturationLevelClient(SQLHandler.loadFloat(uuid, "saturation"));
    }

    public static void convert(PlayerEntity player) {
        String uuid = player.getUuid().toString();
        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);
    }
}

