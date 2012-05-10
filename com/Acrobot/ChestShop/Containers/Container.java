package com.Acrobot.ChestShop.Containers;

import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public interface Container {
    public boolean isEmpty();

    public void addItem(ItemStack item, int amount);

    public void removeItem(ItemStack item, short durability, int amount);

    public int amount(ItemStack item, short durability);

    public boolean hasEnough(ItemStack item, int amount, short durability);

    public boolean fits(ItemStack item, int amount, short durability);
}
