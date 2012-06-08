package com.Acrobot.ChestShop.Containers;

import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class AdminChest implements Container {
    public boolean isEmpty() {
        return false;
    }

    public void addItem(ItemStack item) {
    }

    public void removeItem(ItemStack item) {
    }

    public int amount(ItemStack item) {
        return Integer.MAX_VALUE;
    }

    public boolean hasEnough(ItemStack item) {
        return true;
    }

    public boolean fits(ItemStack item) {
        return true;
    }
}
