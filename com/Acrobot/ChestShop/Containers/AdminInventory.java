package com.Acrobot.ChestShop.Containers;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Acrobot
 */
public class AdminInventory implements Inventory {
    public int getSize() {
        return Integer.MAX_VALUE;
    }

    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }

    public void setMaxStackSize(int i) {
    }

    public String getName() {
        return "Admin inventory";
    }

    public ItemStack getItem(int i) {
        return null;
    }

    public void setItem(int i, ItemStack itemStack) {
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) {
        return new HashMap<Integer, ItemStack>();
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) {
        return new HashMap<Integer, ItemStack>();
    }

    public ItemStack[] getContents() {
        return new ItemStack[]{
                new ItemStack(Material.CHEST, 1),
                new ItemStack(Material.AIR, Integer.MAX_VALUE)
        };
    }

    public void setContents(ItemStack[] itemStacks) {
    }

    public boolean contains(int i) {
        return true;
    }

    public boolean contains(Material material) {
        return true;
    }

    public boolean contains(ItemStack itemStack) {
        return true;
    }

    public boolean contains(int i, int i1) {
        return true;
    }

    public boolean contains(Material material, int i) {
        return true;
    }

    public boolean contains(ItemStack itemStack, int i) {
        return true;
    }

    public HashMap<Integer, ? extends ItemStack> all(int i) {
        HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
        items.put(1, new ItemStack(i, Integer.MAX_VALUE));

        return items;
    }

    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        if (material.getMaxDurability() != 0) {
            HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

            for (short currentDurability = 0; currentDurability < material.getMaxDurability(); currentDurability++) {
                items.put((int) currentDurability, new ItemStack(material, Integer.MAX_VALUE, currentDurability));
            }

            return items;
        }

        return all(material.getId());
    }

    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

        ItemStack clone = itemStack.clone();
        clone.setAmount(Integer.MAX_VALUE);

        items.put(1, clone);

        return items;
    }

    public int first(int i) {
        return 0;
    }

    public int first(Material material) {
        return 0;
    }

    public int first(ItemStack itemStack) {
        return 0;
    }

    public int firstEmpty() {
        return 0;
    }

    public void remove(int i) {
    }

    public void remove(Material material) {
    }

    public void remove(ItemStack itemStack) {
    }

    public void clear(int i) {
    }

    public void clear() {
    }

    public List<HumanEntity> getViewers() {
        return new ArrayList<HumanEntity>();
    }

    public String getTitle() {
        return "Admin inventory";
    }

    public InventoryType getType() {
        return null;
    }

    public InventoryHolder getHolder() {
        return null;
    }

    public ListIterator<ItemStack> iterator() {
        return null;
    }

    public ListIterator<ItemStack> iterator(int i) {
        return null;
    }
}
