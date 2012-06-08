package com.Acrobot.Breeze.Utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
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
     * Counts leftovers from a map
     *
     * @param items Leftovers
     * @return Number of leftovers
     */
    private static int countItems(Map<Integer, ?> items) {
        int totalLeft = 0;

        for (int left : items.keySet()) {
            totalLeft += left;
        }

        return totalLeft;
    }
}
