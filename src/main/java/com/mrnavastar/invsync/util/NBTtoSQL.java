package com.mrnavastar.invsync.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class NBTtoSQL {

    public static void convertInventory(PlayerEntity player, String uuid) {
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.inventory.main.get(i);
            SQLHandler.saveItem(uuid, "inv", i, item);
        }
        ItemStack item = player.inventory.offHand.get(0);
        SQLHandler.saveItem(uuid, "offHand", item);

        for (int i = 0; i < 4; i++) {
            item = player.inventory.armor.get(i);
            SQLHandler.saveItem(uuid, "armour", i, item);
        }
    }

    public static void convertEnderChest(PlayerEntity player, String uuid) {
        for (int i = 0; i < 28; i++) {
            ItemStack item = player.getEnderChestInventory().getStack(i);
            SQLHandler.saveItem(uuid, "eChest", i, item);
        }
    }

    public static void convertStats(PlayerEntity player, String uuid) {
        int xp, score, foodLevel;
        float health, saturation;

        xp = player.experienceLevel;
        SQLHandler.saveInt(uuid, "xp", xp);

        score = player.getScore();
        SQLHandler.saveInt(uuid, "score", score);

        health = player.getHealth();
        SQLHandler.saveFloat(uuid, "health", health);

        if (ConfigManager.Sync_Food_Level)
            foodLevel = player.getHungerManager().getFoodLevel();
            saturation = player.getHungerManager().getSaturationLevel();
    }

    public static void convert(PlayerEntity player) {
        String uuid = player.getUuid().toString();
        SQLHandler.createRow(uuid);

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);
    }
}

