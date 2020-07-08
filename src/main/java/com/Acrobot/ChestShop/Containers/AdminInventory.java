package com.Acrobot.ChestShop.Containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.Acrobot.Breeze.Utils.MaterialUtil;
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

    private ItemStack[] content;
    private int maxStackSize = 64;

    public AdminInventory(ItemStack[] content) {
        this.content = content;
    }

    @Override
    public int getSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public void setMaxStackSize(int i) {
        maxStackSize = i;
    }

    public String getName() {
        return "Admin inventory";
    }

    @Override
    public ItemStack getItem(int i) {
        if (content.length < i) {
            return content[i];
        }
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        if (i > getSize()) {
            throw new IllegalArgumentException("Slot is outside inventory. Max size is " + getSize());
        }
        if (i >= content.length) {
            content = Arrays.copyOfRange(content, 0, i);
        }
        content[i] = itemStack;
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) {
        return new HashMap<>();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) {
        return new HashMap<>();
    }

    public HashMap<Integer, ItemStack> removeItemAnySlot(ItemStack... items) throws IllegalArgumentException {
        return new HashMap<>();
    }

    @Override
    public ItemStack[] getContents() {
        return content;
    }

    @Override
    public void setContents(ItemStack[] itemStacks) {
        content = itemStacks;
    }

    @Override
    public ItemStack[] getStorageContents() {
        return content;
    }

    @Override
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        content = itemStacks;
    }

    @Override
    public boolean contains(Material material) {
        return first(material) > -1;
    }

    @Override
    public boolean contains(ItemStack itemStack) {
        return first(itemStack) > -1;
    }

    @Override
    public boolean contains(Material material, int i) {
        int amount = 0;
        for (ItemStack item : content) {
            if (item != null && item.getType() == material) {
                amount += item.getAmount();
            }
        }
        return amount >= i;
    }

    @Override
    public boolean contains(ItemStack itemStack, int i) {
        int amount = 0;
        for (ItemStack item : content) {
            if (MaterialUtil.equals(item, itemStack)) {
                amount += itemStack.getAmount();
            }
        }
        return amount >= i;
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int i) {
        return contains(itemStack, i);
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
        for (int i = 0; i < content.length; i++) {
            if (content[i] != null && content[i].getType() == material) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int first(ItemStack itemStack) {
        for (int i = 0; i < content.length; i++) {
            if (MaterialUtil.equals(content[i], itemStack)) {
                return i;
            }
        }
        return -1;
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

    public InventoryHolder getHolder(boolean useSnapshot) {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return Arrays.asList(content).listIterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int i) {
        return Arrays.asList(content).listIterator(i);
    }

    @Override
    public Location getLocation() {
        return null;
    }
}
