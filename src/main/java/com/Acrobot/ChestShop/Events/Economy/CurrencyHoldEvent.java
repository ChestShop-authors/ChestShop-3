package com.Acrobot.ChestShop.Events.Economy;

import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * Checks if the account can hold the amount of currency
 *
 * @author Acrobot
 */
public class CurrencyHoldEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    boolean canHold = true;

    private BigDecimal amount;
    private String account;
    private World world;

    public CurrencyHoldEvent(BigDecimal amount, String account, World world) {
        this.amount = amount;
        this.account = account;
        this.world = world;
    }

    public CurrencyHoldEvent(BigDecimal amount, Player target) {
        this(amount, NameManager.getUsername(target.getUniqueId()), target.getWorld());
    }

    /**
     * @return Can the account hold the amount of currency?
     */
    public boolean canHold() {
        return canHold;
    }

    /**
     * Sets if the account can hold the amount of currency
     *
     * @param canHold Can the account hold the currency?
     */
    public void canHold(boolean canHold) {
        this.canHold = canHold;
    }

    /**
     * @return Amount of currency
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return Amount of currency, as a double
     * @deprecated Use {@link #getAmount()} if possible
     */
    public double getDoubleAmount() {
        return amount.doubleValue();
    }

    /**
     * Sets the amount of currency to check
     *
     * @param amount Amount to check
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Sets the amount of currency to check
     *
     * @param amount Amount to check
     * @deprecated Use {@link #setAmount(java.math.BigDecimal)} if possible
     */
    public void setAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
    }

    /**
     * @return The world in which the check occurs
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return Account that is checked
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account name
     *
     * @param account Account name
     */
    public void setAccount(String account) {
        this.account = account;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
