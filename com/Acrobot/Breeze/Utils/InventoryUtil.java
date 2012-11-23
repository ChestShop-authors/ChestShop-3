package com.Acrobot.Breeze.Utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Acrobot
 */
public class InventoryUtil {
    /**
     * Returns the amount of the item inside the inventory
     *
     * @param item      Item to check
     * @param inventory inventory
     * @return amount of the item
     */
    public static int getAmount(ItemStack item, Inventory inventory) {
        if (!inventory.contains(item.getType())) {
            return 0;
        }

        if (inventory.getType() == null) {
            return Integer.MAX_VALUE;
        }

        HashMap<Integer, ? extends ItemStack> items = inventory.all(item.getType());
        int itemAmount = 0;

        for (ItemStack iStack : items.values()) {
            if (!MaterialUtil.equals(iStack, item)) {
                continue;
            }

            itemAmount += iStack.getAmount();
        }

        return itemAmount;
    }

    /**
     * Tells if the inventory is empty
     *
     * @param inventory inventory
     * @return Is the inventory empty?
     */
    public static boolean isEmpty(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (!MaterialUtil.isEmpty(stack)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the inventory has stock of this type
     *
     * @param items     items
     * @param inventory inventory
     * @return Does the inventory contain stock of this type?
     */
    public static boolean hasItems(ItemStack[] items, Inventory inventory) {
        for (ItemStack item : items) {
            if (InventoryUtil.getAmount(item, inventory) < item.getAmount()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the item fits the inventory
     *
     * @param item      Item to check
     * @param inventory inventory
     * @return Does item fit inside inventory?
     */
    public static boolean fits(ItemStack item, Inventory inventory) {
        int left = item.getAmount();

        for (ItemStack iStack : inventory.getContents()) {
            if (left <= 0) {
                return true;
            }

            if (MaterialUtil.isEmpty(iStack)) {
                left -= inventory.getMaxStackSize();
                continue;
            }

            if (!MaterialUtil.equals(iStack, item)) {
                continue;
            }

            left -= (iStack.getMaxStackSize() - iStack.getAmount());
        }

        return left <= 0;
    }

    /**
     * Adds an item to the inventory
     *
     * @param item      Item to add
     * @param inventory Inventory
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.addItem(item);

        return countItems(leftovers);
    }

    /**
     * Removes an item from the inventory
     *
     * @param item      Item to remove
     * @param inventory Inventory
     * @return Number of items that couldn't be removed
     */
    public static int remove(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.removeItem(item);

        return countItems(leftovers);
    }

    /**
     * If items in arguments are similar, this function merges them into stacks of the same type
     *
     * @param items Items to merge
     * @return Merged stack array
     */
    public static ItemStack[] mergeSimilarStacks(ItemStack... items) {
        List<ItemStack> itemList = new LinkedList<ItemStack>();

        for (ItemStack item : items) {
            boolean added = false;

            for (ItemStack iStack : itemList) {
                if (MaterialUtil.equals(item, iStack)) {
                    iStack.setAmount(iStack.getAmount() + item.getAmount());
                    added = true;
                    break;
                }
            }

            if (!added) {
                itemList.add(item);
            }
        }

        return itemList.toArray(new ItemStack[itemList.size()]);
    }

    /**
     * Counts leftovers from a map
     *
     * @param items Leftovers
     * @return Number of leftovers
     */
    public static int countItems(Map<Integer, ?> items) {
        int totalLeft = 0;

        for (int left : items.keySet()) {
            totalLeft += left;
        }

        return totalLeft;
    }
}
