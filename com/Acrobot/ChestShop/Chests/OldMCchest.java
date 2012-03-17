package com.Acrobot.ChestShop.Chests;

import com.Acrobot.ChestShop.BukkitFixes.bInventoryFix;
import com.Acrobot.ChestShop.Utils.uInventory;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class OldMCchest implements ChestObject {
    private final Inventory inventory;

    public OldMCchest(Chest chest) {
        this.inventory = bInventoryFix.getInventory(chest);
    }

    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    public void setSlot(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void clearSlot(int slot) {
        inventory.clear(slot);
    }

    public void addItem(ItemStack item, int amount) {
        uInventory.add(inventory, item, amount);
    }

    public void removeItem(ItemStack item, short durability, int amount) {
        uInventory.remove(inventory, item, amount, durability);
    }

    public int amount(ItemStack item, short durability) {
        return uInventory.amount(inventory, item, durability);
    }

    public boolean hasEnough(ItemStack item, int amount, short durability) {
        return amount(item, durability) >= amount;
    }

    public boolean fits(ItemStack item, int amount, short durability) {
        return uInventory.fits(inventory, item, amount, durability) <= 0;
    }

    public int getSize() {
        return inventory.getSize();
    }
}
