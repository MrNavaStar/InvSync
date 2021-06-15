package com.mrnavastar.invsync.conversion;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class ConversionHelpers {

    public static String itemStackToString(ItemStack item) {
        String itemData = item.writeNbt(new NbtCompound()).toString();
        return itemData.replace("'", "$");
    }

    public static String foodLevelToString(HungerManager hungerManager) {
        NbtCompound tag = new NbtCompound();
        hungerManager.writeNbt(tag);
        return tag.toString();
    }

    public static String effectsToString(StatusEffectInstance effectInstance) {
        NbtCompound tag = new NbtCompound();
        effectInstance.writeNbt(tag);
        return tag.toString();
    }

    public static NbtCompound stringToTag(String nbt) {
        NbtCompound tag = null;
        try {
            tag = StringNbtReader.parse(nbt.replace("$", "'"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return tag;
    }
}