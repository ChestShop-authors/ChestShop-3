package com.Acrobot.ChestShop.Containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class AdminInventory implements Inventory {
    @Override
    public int getSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setMaxStackSize(int i) {
    }

    @Override
    public String getName() {
        return "Admin inventory";
    }

    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) {
        return new HashMap<>();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) {
        return new HashMap<>();
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{
                new ItemStack(Material.CHEST, 1),
                new ItemStack(Material.AIR, Integer.MAX_VALUE)
        };
    }

    @Override
    public void setContents(ItemStack[] itemStacks) {
    }

    @Override
    public ItemStack[] getStorageContents() {
        return new ItemStack[0];
    }

    @Override
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {

    }

    @Override
    public boolean contains(Material material) {
        return true;
    }

    @Override
    public boolean contains(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean contains(Material material, int i) {
        return true;
    }

    @Override
    public boolean contains(ItemStack itemStack, int i) {
        return true;
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int i) {
        return true;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        if (material.getMaxDurability() != 0) {

            for (short currentDurability = 0; currentDurability < material.getMaxDurability(); currentDurability++) {
                items.put((int) currentDurability, new ItemStack(material, Integer.MAX_VALUE, currentDurability));
            }

            return items;
        }

        items.put(1, new ItemStack(material, Integer.MAX_VALUE));
        return items;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

        ItemStack clone = itemStack.clone();
        clone.setAmount(Integer.MAX_VALUE);

        items.put(1, clone);

        return items;
    }

    @Override
    public int first(Material material) {
        return 0;
    }

    @Override
    public int first(ItemStack itemStack) {
        return 0;
    }

    @Override
    public int firstEmpty() {
        return 0;
    }

    @Override
    public void remove(Material material) {
    }

    @Override
    public void remove(ItemStack itemStack) {
    }

    @Override
    public void clear(int i) {
    }

    @Override
    public void clear() {
    }

    @Override
    public List<HumanEntity> getViewers() {
        return new ArrayList<>();
    }

    @Override
    public String getTitle() {
        return "Admin inventory";
    }

    @Override
    public InventoryType getType() {
        return null;
    }

    @Override
    public InventoryHolder getHolder() {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return Collections.emptyListIterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int i) {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }
}
