package com.mrnavastar.invsync.conversion;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

public class ConversionHelpers {

    public static String itemStackToString(ItemStack item) {
        String itemData = item.toTag(new CompoundTag()).toString();
        return itemData.replace("'", "$");
    }

    public static String foodLevelToString(HungerManager hungerManager) {
        CompoundTag tag = new CompoundTag();
        hungerManager.toTag(tag);
        return tag.toString();
    }

    public static String effectsToString(StatusEffectInstance effectInstance) {
        CompoundTag tag = new CompoundTag();
        effectInstance.toTag(tag);
        return tag.toString();
    }

    public static CompoundTag stringToTag(String nbt) {
        CompoundTag tag = null;
        try {
            tag = StringNbtReader.parse(nbt.replace("$", "'"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return tag;
    }
}