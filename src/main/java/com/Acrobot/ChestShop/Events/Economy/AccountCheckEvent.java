package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Checks for the existance of an account
 *
 * @author Acrobot
 */
public class AccountCheckEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    boolean outcome;

    private String account;
    private World world;

    public AccountCheckEvent(String account, World world) {
        this.account = account;
        this.world = world;
    }

    public AccountCheckEvent(String account) {
        this.account = account;
    }

    /**
     * @return Event's outcome (does the account exist?)
     */
    public boolean hasAccount() {
        return outcome;
    }

    /**
     * Sets the event's outcome
     *
     * @param outcome Outcome of the check
     */
    public void hasAccount(boolean outcome) {
        this.outcome = outcome;
    }

    /**
     * @return Account which is being checked
     */
    public String getAccount() {
        return account;
    }

    /**
     * @return The world in which the check occurs
     */
    public World getWorld() {
        return world;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
