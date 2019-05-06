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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
