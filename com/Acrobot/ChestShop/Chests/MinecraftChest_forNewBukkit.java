package com.Acrobot.ChestShop.Chests;

import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uInventory;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class MinecraftChest_forNewBukkit implements ChestObject {
    private final Chest chest;

    public MinecraftChest_forNewBukkit(Chest chest) {
        this.chest = chest;
    }

    public ItemStack[] getContents() {
        return chest.getInventory().getContents();
    }

    public void setSlot(int slot, ItemStack item) {
        chest.getInventory().setItem(slot, item);
    }

    public void clearSlot(int slot) {
        chest.getInventory().setItem(slot, null);
    }

    public void addItem(ItemStack item, int amount) {
        uInventory.add(chest.getInventory(), item, amount);
    }

    public void removeItem(ItemStack item, short durability, int amount) {
        uInventory.remove(chest.getInventory(), item, amount, durability);
    }

    public int amount(ItemStack item, short durability) {
        return uInventory.amount(chest.getInventory(), item, durability);
    }

    public boolean hasEnough(ItemStack item, int amount, short durability) {
        return amount(item, durability) >= amount;
    }

    public boolean fits(ItemStack item, int amount, short durability) {
        return uInventory.fits(chest.getInventory(), item, amount, durability) <= 0;
    }

    public int getSize() {
        return chest.getInventory().getSize();
    }

    public Chest getNeighbor() {
        return uBlock.findNeighbor(chest);
    }
}
