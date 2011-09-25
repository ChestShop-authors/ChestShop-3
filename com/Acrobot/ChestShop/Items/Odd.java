package com.Acrobot.ChestShop.Items;

import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Odd {
    public static boolean isInitialized;

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static ItemStack returnItemStack(String name) {
        try {
            return OddItem.getItemStack(name);
        } catch (Exception ignored) {
            return null;
        }
    }
}
