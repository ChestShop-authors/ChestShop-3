package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Checks if the account can hold the amount of currency
 *
 * @author Acrobot
 */
public class CurrencyHoldEvent extends EconomicEvent {
    private static final HandlerList handlers = new HandlerList();

    private boolean canHold = true;

    private BigDecimal amount;
    private UUID account;
    private World world;

    public CurrencyHoldEvent(BigDecimal amount, UUID account, World world) {
        this.amount = amount;
        this.account = account;
        this.world = world;
    }

    public CurrencyHoldEvent(BigDecimal amount, Player target) {
        this(amount, target.getUniqueId(), target.getWorld());
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
    public UUID getAccount() {
        return account;
    }

    /**
     * Sets the account name
     *
     * @param account Account name
     */
    public void setAccount(UUID account) {
        this.account = account;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
