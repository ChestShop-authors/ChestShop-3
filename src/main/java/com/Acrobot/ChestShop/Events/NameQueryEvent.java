package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a request whether a name is allowed.
 */
public class NameQueryEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private boolean valid;

    public NameQueryEvent(String name) {
        this.name = name;
    }

    /**
     * The name to check for.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Whether or not the name is valid.
     *
     * @return Whether the name is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Set whether the name is valid.
     *
     * @param valid Whether the name is valid
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
