package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemStringQueryEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private String itemString = null;
    private final ItemStack item;

    /**
     * Query the item string representation of a certain item with a certain length
     * @param item      The item to query the string for
     */
    public ItemStringQueryEvent(ItemStack item) {
        this.item = item;
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
}
