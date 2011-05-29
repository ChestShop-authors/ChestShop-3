package com.Acrobot.ChestShop.Items;

import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Odd {
    public static OddItem oddItem;

    public static boolean isInitialized() {
        return oddItem != null;
    }

    public static ItemStack returnItemStack(String name) {
        try {
            return oddItem.getItemStack(name);
        } catch (Exception ignored) {
            return null;
        }
    }
}
