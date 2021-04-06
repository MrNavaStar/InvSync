package com.mrnavastar.invsync.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

//This is a kinda yucky implementation, but hey if it works it works
public class ConversionHelpers {

    public static String itemStackToString(ItemStack item) {
        String itemData = item.toTag(new CompoundTag()).toString();
        return itemData.replace("'", "$");
    }

    public static ItemStack stringToItemStack(String nbt) {
        nbt = nbt.replace("$", "'");
        CompoundTag tag = null;
        try {
            tag = StringNbtReader.parse(nbt);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return ItemStack.fromTag(tag);
    }
}