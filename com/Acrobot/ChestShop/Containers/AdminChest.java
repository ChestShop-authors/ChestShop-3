package com.Acrobot.ChestShop.Containers;

import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class AdminChest implements Container {
    public boolean isEmpty() {
        return false;
    }

    public void addItem(ItemStack item, int amount) {
    }

    public void removeItem(ItemStack item, short durability, int amount) {
    }

    public int amount(ItemStack item, short durability) {
        return Integer.MAX_VALUE;
    }

    public boolean hasEnough(ItemStack item, int amount, short durability) {
        return true;
    }

    public boolean fits(ItemStack item, int amount, short durability) {
        return true;
    }
}
