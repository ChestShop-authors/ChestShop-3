package com.Acrobot.Breeze.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.google.common.collect.ImmutableMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class InventoryUtil {
    private static Boolean legacyContents = null;

    private static ItemStack[] getStorageContents(Inventory inventory) {
        if (legacyContents == null) {
            try {
                inventory.getStorageContents();
                legacyContents = false;
            } catch (NoSuchMethodError e) {
                legacyContents = true;
            }
        }

        return legacyContents ? inventory.getContents() : inventory.getStorageContents();
    }

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
        for (ItemStack stack : getStorageContents(inventory)) {
            if (!MaterialUtil.isEmpty(stack)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Count amount of empty slots in an inventory
     *
     * @param inventory the inventory
     * @return The amount of empty slots
     */
    public static int countEmpty(Inventory inventory) {
        int emptyAmount = 0;
        for (ItemStack stack : getStorageContents(inventory)) {
            if (MaterialUtil.isEmpty(stack)) {
                emptyAmount++;
            }
        }

        return emptyAmount;
    }

    /**
     * Checks if the inventory has stock of this type
     *
     * @param items     items
     * @param inventory inventory
     * @return Does the inventory contain stock of this type?
     */
    public static boolean hasItems(ItemStack[] items, Inventory inventory) {
        Map<ItemStack, Integer> itemCounts = getItemCounts(items);
        for (Map.Entry<ItemStack, Integer> entry : itemCounts.entrySet()) {
            if (getAmount(entry.getKey(), inventory) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if items fit in the inventory
     *
     * @param items     Items to check
     * @param inventory inventory
     * @return Do the items fit inside the inventory?
     */
    public static boolean fits(ItemStack[] items, Inventory inventory) {
        Map<ItemStack, Integer> itemCounts = InventoryUtil.getItemCounts(items);
        for (Map.Entry<ItemStack, Integer> entry : itemCounts.entrySet()) {
            if (!InventoryUtil.fits(entry.getKey(), entry.getValue(), inventory)) {
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
        return fits(item, item.getAmount(), inventory);
    }

    /**
     * Checks if the item fits the inventory
     *
     * @param item      Item to check
     * @param amount    Amount of the item to check
     * @param inventory inventory
     * @return Does item fit inside inventory?
     */
    public static boolean fits(ItemStack item, int amount,Inventory inventory) {
        int left = amount;
        if (inventory.getSize() == Integer.MAX_VALUE) {
            return true;
        }

        for (ItemStack iStack : getStorageContents(inventory)) {
            if (left <= 0) {
                return true;
            }

            if (MaterialUtil.isEmpty(iStack)) {
                left -= getMaxStackSize(item);
                continue;
            }

            if (!MaterialUtil.equals(iStack, item)) {
                continue;
            }

            left -= (getMaxStackSize(iStack) - iStack.getAmount());
        }

        return left <= 0;
    }

    /**
     * Transfers an item from one inventory to another one
     *
     * @param item              Item to transfer
     * @param sourceInventory   Inventory to transfer the item from
     * @param targetInventory   Inventory to transfer the item to
     * @return Number of leftover items
     */
    public static int transfer(ItemStack item, Inventory sourceInventory, Inventory targetInventory) {
        return transfer(item, sourceInventory, targetInventory, item.getMaxStackSize());
    }

    /**
     * Transfers an item from one inventory to another one
     *
     * @param item              Item to transfer
     * @param sourceInventory   Inventory to transfer the item from
     * @param targetInventory   Inventory to transfer the item to
     * @param maxStackSize      Maximum item's stack size
     * @return Number of leftover items
     */
    public static int transfer(ItemStack item, Inventory sourceInventory, Inventory targetInventory, int maxStackSize) {
        if (item.getAmount() < 1) {
            return 0;
        }

        int amount = item.getAmount();
        for (ItemStack currentItem : sourceInventory) {
            if (MaterialUtil.equals(currentItem, item)) {
                ItemStack clone = currentItem.clone();
                if (currentItem.getAmount() >= amount) {
                    clone.setAmount(amount);
                    amount = 0;
                } else {
                    clone.setAmount(currentItem.getAmount());
                    amount -= clone.getAmount();
                }
                int leftOver = add(clone, targetInventory, maxStackSize);
                if (leftOver > 0) {
                    currentItem.setAmount(currentItem.getAmount() - clone.getAmount() + leftOver);
                    if (amount > 0) {
                        amount += leftOver;
                    } else {
                        return leftOver;
                    }
                } else {
                    currentItem.setAmount(currentItem.getAmount() - clone.getAmount());
                }
            }
            if (amount <= 0) {
                break;
            }
        }
        return amount;
    }

    /**
     * Adds an item to the inventory with given maximum stack size
     *
     * @param item         Item to add
     * @param inventory    Inventory
     * @param maxStackSize Maximum item's stack size
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory, int maxStackSize) {
        if (item.getAmount() < 1) {
            return 0;
        }

        if (maxStackSize == item.getMaxStackSize()) {
            return add(item, inventory);
        }

        return addManually(item, inventory, maxStackSize);
    }

    private static int addManually(ItemStack item, Inventory inventory, int maxStackSize) {
        int amountLeft = item.getAmount();

        for (int currentSlot = 0; currentSlot < effectiveSize(inventory) && amountLeft > 0; currentSlot++) {
            ItemStack currentItem = inventory.getItem(currentSlot);

            if (MaterialUtil.isEmpty(currentItem)) {
                currentItem = new ItemStack(item);
                currentItem.setAmount(Math.min(amountLeft, maxStackSize));
                inventory.setItem(currentSlot, currentItem);

                amountLeft -= currentItem.getAmount();
            } else if (currentItem.getAmount() < maxStackSize && MaterialUtil.equals(currentItem, item)) {
                int neededToAdd = Math.min(maxStackSize - currentItem.getAmount(), amountLeft);

                currentItem.setAmount(currentItem.getAmount() + neededToAdd);

                amountLeft -= neededToAdd;
            }
        }
        return amountLeft;
    }

    // Don't use the armor slots or extra slots
    private static int effectiveSize(Inventory inventory) {
        return getStorageContents(inventory).length;
    }

    /**
     * Adds an item to the inventor
     *
     * @param item      Item to add
     * @param inventory Inventory
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.addItem(item.clone()); // item needs to be cloned as cb changes the amount of the stack size

        if (!leftovers.isEmpty()) {
            for (Iterator<ItemStack> iterator = leftovers.values().iterator(); iterator.hasNext(); ) {
                ItemStack left = iterator.next();
                int amountLeft = addManually(left, inventory, left.getMaxStackSize());
                if (amountLeft == 0) {
                    iterator.remove();
                } else {
                    left.setAmount(amountLeft);
                }
            }
        }

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

        if (!leftovers.isEmpty()) {
            leftovers.values().removeIf(left -> removeManually(left, inventory) == 0);
        }

        return countItems(leftovers);
    }

    private static int removeManually(ItemStack item, Inventory inventory) {
        int amountLeft = item.getAmount();

        for (int currentSlot = 0; currentSlot < effectiveSize(inventory) && amountLeft > 0; currentSlot++) {
            ItemStack currentItem = inventory.getItem(currentSlot);

            if (currentItem != null && MaterialUtil.equals(currentItem, item)) {
                int neededToRemove = Math.min(currentItem.getAmount(), amountLeft);

                currentItem.setAmount(currentItem.getAmount() - neededToRemove);
                inventory.setItem(currentSlot, currentItem);

                amountLeft -= neededToRemove;
            }
        }
        return amountLeft;
    }

    /**
     * If items in arguments are similar, this function merges them into stacks of the same type
     *
     * @param items Items to merge
     * @return Merged stack array
     * @deprecated This produces items that are larger than the max stack size, use either {@link #getItemsStacked(ItemStack...)} or {@link #getItemCounts(ItemStack...)} instead
     */
    @Deprecated
    public static ItemStack[] mergeSimilarStacks(ItemStack... items) {
        if (items.length <= 1) {
            return items;
        }

        List<ItemStack> itemList = new LinkedList<ItemStack>();

        Iterating:
        for (ItemStack item : items) {
            for (ItemStack iStack : itemList) {
                if (MaterialUtil.equals(item, iStack)) {
                    iStack.setAmount(iStack.getAmount() + item.getAmount());
                    continue Iterating;
                }
            }

            itemList.add(item.clone());
        }

        return itemList.toArray(new ItemStack[0]);
    }

    /**
     * If items in arguments are similar, this function counts them
     *
     * @param items Items to count
     * @return The map of items and their amounts. The keys are clones of the original items with their amounts set to 1.
     */
    public static Map<ItemStack, Integer> getItemCounts(ItemStack... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyMap();
        }
        if (items.length == 1) {
            ItemStack itemClone = items[0].clone();
            itemClone.setAmount(1);
            return ImmutableMap.of(itemClone, items[0].getAmount());
        }

        Map<ItemStack, Integer> counts = new LinkedHashMap<>();

        Iterating:
        for (ItemStack item : items) {
            for (Map.Entry<ItemStack, Integer> entry : counts.entrySet()) {
                if (MaterialUtil.equals(item, entry.getKey())) {
                    entry.setValue(entry.getValue() + item.getAmount());
                    continue Iterating;
                }
            }

            ItemStack itemClone = item.clone();
            itemClone.setAmount(1);
            counts.put(itemClone, item.getAmount());
        }

        return counts;
    }

    /**
     * Counts the amount of items in ItemStacks
     *
     * @param items ItemStacks of items to count
     * @return How many items are there?
     */
    public static int countItems(ItemStack... items) {
        int count = 0;

        for (ItemStack item : items) {
            count += item.getAmount();
        }

        return count;
    }

    /**
     * Counts leftovers from a map
     *
     * @param items Leftovers
     * @return Number of leftovers
     */
    public static int countItems(Map<Integer, ItemStack> items) {
        int totalLeft = 0;

        for (ItemStack left : items.values()) {
            totalLeft += left.getAmount();
        }

        return totalLeft;
    }

    /**
     * Get the max size an item stack is allowed to stack to while respecting the STACK_TO_64 config property
     *
     * @param item The item to get the max stacksize of
     * @return The max stacksize of the item stack's type or 64 if STACK_TO_64 is enabled
     */
    public static int getMaxStackSize(ItemStack item) {
        return Properties.STACK_TO_64 ? 64 : item.getMaxStackSize();
    }

    /**
     * Get an array of different item stacks that are properly stacked to their max stack size
     *
     * @param items The items to stack
     * @return An array of item stacks which's amount is a maximum of the allowed stack size
     */
    public static ItemStack[] getItemsStacked(ItemStack... items) {
        List<ItemStack> stackedItems = new LinkedList<>();
        for (ItemStack item : items) {
            stackItems(stackedItems, item, item.getAmount());
        }
        return stackedItems.toArray(new ItemStack[0]);
    }

    /**
     * Get an array of different item stacks that are properly stacked to their max stack size
     *
     * @param item      The item to stack
     * @param amount    The amount of the item to stack
     * @return An array of item stacks which's amount is a maximum of the allowed stack size
     */
    public static ItemStack[] getItemStacked(ItemStack item, int amount) {
        return stackItems(new LinkedList<>(), item, amount).toArray(new ItemStack[0]);
    }

    /**
     * Add properly stacked items to a collection
     *
     * @param stackedItems The collection to add the items to
     * @param item      The item to stack
     * @param amount    The amount of the item to stack
     * @return The input collection
     */
    private static Collection<ItemStack> stackItems(Collection<ItemStack> stackedItems, ItemStack item, int amount) {
        int maxStackSize = getMaxStackSize(item);
        if (maxStackSize == 0) {
            return stackedItems;
        }

        ItemStack itemClone = item.clone();

        for (ItemStack stackedItem : stackedItems) {
            if (MaterialUtil.equals(stackedItem, itemClone) && stackedItem.getAmount() < getMaxStackSize(stackedItem)) {
                int amountToAdd = Math.min(getMaxStackSize(stackedItem) - stackedItem.getAmount(), amount);
                stackedItem.setAmount(stackedItem.getAmount() + amountToAdd);
                amount = amount - amountToAdd;
                if (amount <= 0) {
                    break;
                }

            }
        }
        if (amount > maxStackSize || amount <= 0) {
            for (int i = 0; i < Math.floor((double) amount / maxStackSize); i++) {
                ItemStack itemAddClone = itemClone.clone();
                itemAddClone.setAmount(maxStackSize);
                stackedItems.add(itemAddClone);
            }
            amount = amount % maxStackSize;
        }
        if (amount > 0) {
            itemClone.setAmount(amount);
            stackedItems.add(itemClone);
        }
        return stackedItems;
    }
}
