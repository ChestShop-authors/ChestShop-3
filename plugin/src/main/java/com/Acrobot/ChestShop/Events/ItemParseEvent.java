package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemParseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final String itemString;
    private ItemStack item = null;

    public ItemParseEvent(String itemString) {
        this.itemString = itemString;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the item string that should be parsed
     * @return The item string to parse
     */
    public String getItemString() {
        return itemString;
    }

    /**
     * Set the item that the string represents
     * @param item The item for the string
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * The item that was parsed
     * @return The parsed item or null if none was found
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Whether or not the item string of this event has a parsed item
     * @return True if an item was successfully parsed; false if not
     */
    public boolean hasItem() {
        return item != null;
    }
}
