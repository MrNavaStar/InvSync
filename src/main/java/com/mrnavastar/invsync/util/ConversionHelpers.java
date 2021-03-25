package com.mrnavastar.invsync.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ConversionHelpers {

    public static String itemStackToString(ItemStack item) {
        String itemData = item.toTag(new CompoundTag()).toString();
        return itemData.replace("'", "$");
    }

    public static ItemStack stringToItemStack(String sqlData) {
        sqlData = sqlData.replace("$", " ");
        return ItemStack.fromTag(new CompoundTag().getCompound(sqlData));
    }
}
