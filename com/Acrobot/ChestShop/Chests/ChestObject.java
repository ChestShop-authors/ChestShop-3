package com.Acrobot.ChestShop.Chests;

import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public interface ChestObject {
    public ItemStack[] getContents();

    public void setSlot(int slot, ItemStack item);

    public void clearSlot(int slot);

    public void addItem(ItemStack item, int amount);

    public void removeItem(ItemStack item, short durability, int amount);

    public int amount(ItemStack item, short durability);

    public boolean hasEnough(ItemStack item, int amount, short durability);

    public boolean fits(ItemStack item, int amount, short durability);

    public int getSize();
}
