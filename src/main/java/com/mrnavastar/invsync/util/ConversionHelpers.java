package com.mrnavastar.invsync.util;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class ConversionHelpers {

    public static String itemStackToString(ItemStack item) {
        String itemData = item.getItem() + " " + item.getCount() + " " + item.getTag();
        itemData = itemData.replace("'", "$");
        return itemData;
    }
}
