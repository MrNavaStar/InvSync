package arnaria.invsync.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import sun.security.x509.CertificatePolicyMap;

import java.util.UUID;

public class NBTtoSQL {

    public static void convertInventory(PlayerEntity player, UUID uuid) {
        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.inventory.main.get(i);
                System.out.println("inv " + i + ": " + item);
            }
            ItemStack item = player.inventory.offHand.get(0);
            System.out.println("Off Hand: " + item);
        }

        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                ItemStack item = player.inventory.armor.get(i);
                System.out.println("armour: " + item);
            }
        }
    }

    public static void convertStats(PlayerEntity player, UUID uuid) {
        int xp, score, foodLevel;
        float health, saturation;

        if (ConfigManager.Sync_Xp) xp = player.experienceLevel;
        if (ConfigManager.Sync_Score) score = player.getScore();
        if (ConfigManager.Sync_Health) health = player.getHealth();
        if (ConfigManager.Sync_Food_Level) foodLevel = player.getHungerManager().getFoodLevel();
        if (ConfigManager.Sync_Saturation) saturation = player.getHungerManager().getSaturationLevel();
    }

    public static void convertEnderChest(PlayerEntity player, UUID uuid) {
        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 28; i++) {
                ItemStack item = player.getEnderChestInventory().getStack(i);
                System.out.println("ender: " + i + ": " + item);
            }
        }
    }

    public static void convert(PlayerEntity player) {
        UUID uuid = player.getUuid();

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);
    }
}

