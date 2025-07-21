package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemStringQueryEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private String itemString = null;
    private final ItemStack item;
    private final int maxWidth;

    /**
     * Query the item string representation of a certain item with a certain length
     * @param item      The item to query the string for
     */
    public ItemStringQueryEvent(ItemStack item) {
        this(item, 0);
    }

    /**
     * Query the item string representation of a certain item with a certain length
     * @param item      The item to query the string for
     * @param maxWidth  The max width of the item string
     */
    public ItemStringQueryEvent(ItemStack item, int maxWidth) {
        this.item = item;
        this.maxWidth = maxWidth;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The item for which the string is queried
     * @return The item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Get the item string that represents the item
     * @return The item string that represents the item
     */
    public String getItemString() {
        return itemString;
    }

    /**
     * Set the item string that represents the item
     * @param itemString  The item string that represents the item
     */
    public void setItemString(String itemString) {
        this.itemString = itemString;
    }

    /**
     * Get the max width that the result item string should have
     * @return The max width of the result item string
     */
    public int getMaxWidth() {
        return maxWidth;
    }
}
