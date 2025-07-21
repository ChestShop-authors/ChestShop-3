package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Checks for the existence of an account
 *
 * @author Acrobot
 */
public class AccountCheckEvent extends EconomicEvent {
    private static final HandlerList handlers = new HandlerList();

    private boolean outcome = false;

    private UUID account;
    private World world;

    public AccountCheckEvent(UUID account, World world) {
        this.account = account;
        this.world = world;
    }

    public AccountCheckEvent(UUID account) {
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
    public UUID getAccount() {
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
