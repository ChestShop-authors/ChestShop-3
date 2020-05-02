package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Database.Account;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a query for an account by using the name (e.g. from the shop sign)
 */
public class AccountQueryEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private Account account = null;
    private boolean searchOfflinePlayers = true;

    public AccountQueryEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Get whether or not offline player data should be searched (too)
     * @return Whether or not offline player data should be searched (too)
     */
    public boolean searchOfflinePlayers() {
        return searchOfflinePlayers;
    }

    /**
     * Set whether or not offline player data should be searched (too)
     * @param searchOfflinePlayers Whether or not offline player data should be searched (too)
     */
    public void searchOfflinePlayers(boolean searchOfflinePlayers) {
        this.searchOfflinePlayers = searchOfflinePlayers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
