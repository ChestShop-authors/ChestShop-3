package com.Acrobot.ChestShop.Containers;

import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public interface Container {
    boolean isEmpty();

    void addItem(ItemStack item);

    void removeItem(ItemStack item);

    int amount(ItemStack item);

    boolean hasEnough(ItemStack item);

    boolean fits(ItemStack item);
}
