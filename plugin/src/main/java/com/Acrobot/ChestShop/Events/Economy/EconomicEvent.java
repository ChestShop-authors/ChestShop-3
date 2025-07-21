package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.event.Event;

public abstract class EconomicEvent extends Event {

    private boolean handled = false;

    /**
     * Get whether or not this event was successfully handled by a listener
     *
     * @return Whether or not the amount was successfully handled
     */
    public boolean wasHandled() {
        return handled;
    }

    /**
     * Set whether or not this event was successfully handled by a listener
     *
     * @param handled Whether or not the amount was successfully handled
     */
    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}
