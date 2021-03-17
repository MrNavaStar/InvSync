package arnaria.invsync;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class NBTtoSQL {

    public static void convertInventory(PlayerEntity player, UUID uuid) {
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.inventory.main.get(i);
            System.out.println("inv "+ i + ": " + item);
        }
        for (int i = 0; i < 4; i++) {
            ItemStack item = player.inventory.armor.get(i);
            System.out.println("armour: " + item);
        }
        ItemStack item = player.inventory.offHand.get(0);
        System.out.println("Off Hand: " + item);
    }

    public static void convertStats(PlayerEntity player, UUID uuid) {
        int xp = player.experienceLevel;
        int score = player.getScore();
        float health = player.getHealth();
        int foodLevel = player.getHungerManager().getFoodLevel();
        float saturation = player.getHungerManager().getSaturationLevel();
        System.out.println("stats: " + xp + " " + score + " " + health + " " + foodLevel + " " + saturation);
    }

    public static void convertEnderChest(PlayerEntity player, UUID uuid) {
        for (int i = 0; i < 28; i++) {
            ItemStack item = player.getEnderChestInventory().getStack(i);
            System.out.println("ender: " + i + ": " + item);
        }
    }

    public static void convert(PlayerEntity player) {
        UUID uuid = player.getUuid();

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);
    }
}

