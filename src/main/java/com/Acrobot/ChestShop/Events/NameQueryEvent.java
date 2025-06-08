package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NameQueryEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private boolean valid;

    public NameQueryEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return valid;
    }

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
