package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a request whether all sign lines are valid.
 */
public class SignValidationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String[] lines;
    private boolean valid;

    public SignValidationEvent(String[] lines) {
        this.lines = lines;
    }

    /**
     * Returns the text on the sign
     *
     * @return Text on the sign
     */
    public String[] getLines() {
        return lines;
    }

    /**
     * Get the owner string of a shop sign.
     *
     * @return The owner string
     */
    public String getOwner() {
        return ChestShopSign.getOwner(lines);
    }

    /**
     * Get the quantity and count line of the shop sign.
     *
     * @return The quantity line
     */
    public String getQuantity() {
        return ChestShopSign.getQuantityLine(lines);
    }

    /**
     * Get the price line of the shop sign
     *
     * @return The price line
     */
    public String getPrice() {
        return ChestShopSign.getPrice(lines);
    }

    /**
     * Get the item line of the shop sign
     *
     * @return The item line
     */
    public String getItem() {
        return ChestShopSign.getItem(lines);
    }

    /**
     * Whether or not the sign is valid.
     *
     * @return Whether the sign is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Set whether the sign is valid.
     *
     * @param valid Whether the sign is valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
