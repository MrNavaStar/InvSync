package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;

import static com.mrnavastar.invsync.setup.PlayerData.playerDataTable;

public class PlayerDataConversionTest {

    public static void nbtToSql(PlayerEntity player) {
        playerDataTable.startTransaction();
        String uuid = player.getUuid().toString();
        playerDataTable.createRow("id", uuid);

        if (ConfigManager.Sync_Inv) {
            ArrayList<String> inv = new ArrayList<>();
            for (int i = 0; i < 36; i++) {
                inv.add(ConversionHelpers.itemStackToString(player.inventory.main.get(i)));
            }
            playerDataTable.saveString(uuid, "inv", inv.toString());
            playerDataTable.saveInt(uuid, "selectedSlot", player.inventory.selectedSlot);
        }

        if (ConfigManager.Sync_Armour) {
            ArrayList<String> armour = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                armour.add(ConversionHelpers.itemStackToString(player.inventory.armor.get(i)));
            }
            playerDataTable.saveString(uuid, "armour", armour.toString());
        }

        if (ConfigManager.Sync_eChest) {
            ArrayList<String> eChest = new ArrayList<>();
            for (int i = 0; i < 27; i++) {
                eChest.add(ConversionHelpers.itemStackToString(player.getEnderChestInventory().getStack(i)));
            }
            playerDataTable.saveString(uuid, "eChest", eChest.toString());
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

        playerDataTable.endTransaction();
    }
}
