package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

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
            playerDataTable.saveString(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0)));
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
            for (StatusEffectInstance e : player.getStatusEffects()) {
                effects.add(ConversionHelpers.effectsToString(e));
            }
            playerDataTable.saveString(uuid, "statusEffects", effects.toString());
        }

        playerDataTable.endTransaction();
    }

    public static void sqlToNbt(PlayerEntity player) {
        playerDataTable.startTransaction();
        String uuid = player.getUuid().toString();

        if (ConfigManager.Sync_Inv) {
            String inv = playerDataTable.loadString(uuid, "inv", null);
            if (inv != null) {
                String[] invArr =  inv.replace("[", "").replace("]", "").split(", ");
                System.out.println(Arrays.toString(invArr));
                for (int i = 0; i < 36; i++) {
                    player.inventory.main.set(i, ItemStack.fromTag(ConversionHelpers.stringToTag(invArr[i])));
                    //System.out.println(invArr[i]);
                }
                player.inventory.offHand.set(0, ItemStack.fromTag(ConversionHelpers.stringToTag(playerDataTable
                        .loadString(uuid, "offHand", ConversionHelpers.itemStackToString(player.inventory.offHand.get(0))))));

                player.inventory.selectedSlot = playerDataTable.loadInt(uuid, "selectedSlot", player.inventory.selectedSlot);
            }
        }

        if (ConfigManager.Sync_Armour) {
            String armour = playerDataTable.loadString(uuid, "armour", null);
            if (armour != null) {
                String[] armourArr =  armour.replace("[", "").replace("]", "").split(", ");
                for (int i = 0; i < 4; i++) {
                    player.inventory.armor.set(i, ItemStack.fromTag(ConversionHelpers.stringToTag(armourArr[i])));
                }
            }
        }

        if (ConfigManager.Sync_eChest) {
            String eChest = playerDataTable.loadString(uuid, "eChest", null);
            if (eChest != null) {
                String[] eChestArr =  eChest.replace("[", "").replace("]", "").split(", ");
                for (int i = 0; i < 27; i++) {
                    player.getEnderChestInventory().setStack(i, ItemStack.fromTag(ConversionHelpers.stringToTag(eChestArr[i])));
                }
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
            String effects = playerDataTable.loadString(uuid, "statusEffects", "[]");
            if (!effects.equals("[]")) {
                String[] effectsArr =  effects.replace("[", "").replace("]", "").split(", ");
                for (String e : effectsArr) {
                    player.addStatusEffect(StatusEffectInstance.fromTag(ConversionHelpers.stringToTag(e)));
                }
            }
        }

        playerDataTable.endTransaction();
    }
}