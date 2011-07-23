package com.Acrobot.ChestShop.Chests;

import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uInventory;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class MinecraftChest implements ChestObject {
    private final Chest main;
    private final Chest neighbor;

    public MinecraftChest(Chest chest) {
        this.main = chest;
        this.neighbor = getNeighbor();
    }

    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[(neighbor != null ? 54 : 27)];
        ItemStack[] chest1 = main.getInventory().getContents();

        System.arraycopy(chest1, 0, contents, 0, chest1.length);

        if (neighbor != null) {
            ItemStack[] chest2 = neighbor.getInventory().getContents();
            System.arraycopy(chest2, 0, contents, chest1.length, chest2.length);
        }

        return contents;
    }

    public void setSlot(int slot, ItemStack item) {
        if (slot < main.getInventory().getSize()) {
            main.getInventory().setItem(slot, item);
        } else {
            neighbor.getInventory().setItem(slot - main.getInventory().getSize(), item);
        }
    }

    public void clearSlot(int slot) {
        if (slot < main.getInventory().getSize()) {
            main.getInventory().setItem(slot, null);
        } else {
            neighbor.getInventory().setItem(slot - main.getInventory().getSize(), null);
        }
    }

    public void addItem(ItemStack item, int amount) {
        int left = addItem(item, amount, main);
        if (neighbor != null && left > 0) addItem(item, left, neighbor);
    }

    public void removeItem(ItemStack item, short durability, int amount) {
        int left = removeItem(item, durability, amount, main);
        if (neighbor != null && left > 0) removeItem(item, durability, left, neighbor);
    }

    public int amount(ItemStack item, short durability) {
        return amount(item, durability, main) + (neighbor != null ? amount(item, durability, neighbor) : 0);
    }

    public boolean hasEnough(ItemStack item, int amount, short durability) {
        return amount(item, durability) >= amount;
    }

    public boolean fits(ItemStack item, int amount, short durability) {
        int firstChest = fits(item, amount, durability, main);
        return (firstChest > 0 && neighbor != null ? fits(item, firstChest, durability, neighbor) <= 0 : firstChest <= 0);
    }

    public int getSize() {
        return main.getInventory().getSize() + (neighbor != null ? neighbor.getInventory().getSize() : 0);
    }

    private Chest getNeighbor() {
        return uBlock.findNeighbor(main);
    }

    private static int amount(ItemStack item, short durability, Chest chest) {
        return uInventory.amount(chest.getInventory(), item, durability);
    }

    private static int fits(ItemStack item, int amount, short durability, Chest chest) {
        return uInventory.fits(chest.getInventory(), item, amount, durability);
    }

    private static int addItem(ItemStack item, int amount, Chest chest) {
        return uInventory.add(chest.getInventory(), item, amount);
    }

    private static int removeItem(ItemStack item, short durability, int amount, Chest chest) {
        return uInventory.remove(chest.getInventory(), item, amount, durability);
    }
}
