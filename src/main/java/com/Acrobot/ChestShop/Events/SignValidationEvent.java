package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a request whether a name is allowed.
 */
public class SignValidationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String[] lines;
    private final String ownerName;
    private boolean valid;

    public SignValidationEvent(String[] lines, String ownerName) {
        this.lines = lines;
        this.ownerName = ownerName;
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
     * The name of the owner to check for.
     *
     * @return The owner's name
     */
    public String getOwnerName() {
        return ownerName;
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
