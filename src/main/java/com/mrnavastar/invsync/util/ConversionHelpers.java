package com.mrnavastar.invsync.util;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class ConversionHelpers {

    public static String serialize(ItemStack item) {
        String[] parts = new String[6];
        parts[0] = item.getItem().toString();
        parts [1] = Integer.toString(item.getCount());
        parts [2] = String.valueOf(item.getDamage());
        parts [3] = item.getName().toString();
        parts [4] = String.valueOf (item.getTag());  //idk if we need this - probably for custom named stuff
        parts [5] = item.getEnchantments().toString();
        return StringUtils.join(parts, ",");
    }

    public static String deserialize(String item) {
        String[] splitArray = item.split(",");
    }
}
