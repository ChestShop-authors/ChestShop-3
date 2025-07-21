package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Database.Account;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an access request for a specific account.
 */
public class AccountAccessEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Account account;
    private boolean canAccess = false;

    public AccountAccessEvent(Player player, Account account) {
        super(player);
        this.account = account;
    }

    /**
     * The account to check the access for
     *
     * @return The account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Whether or not the player can access the account.
     *
     * @return Whether or not the player can access the account
     */
    public boolean canAccess() {
        return canAccess;
    }

    /**
     * Set whether or not the player can access the account.
     *
     * @param canAccess Whether or not the player can access the account
     */
    public void setAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
