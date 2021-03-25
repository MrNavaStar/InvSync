package com.mrnavastar.invsync.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ConversionHelpers {

    public static String itemStackToString(ItemStack item) {
        String itemData = item.toTag(new CompoundTag()).toString();
        itemData = itemData.replace("'", "$");
        return itemData;
    }
    
    public static ItemStack stringToItemStack(String sqlData) {
        sqlData = sqlData.replace("$", " ");
        ItemStack itemStack = ItemStack.fromTag(new CompoundTag().getCompound(sqlData));
        return itemStack;
    }
}
