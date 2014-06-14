package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a check for the existence of specified currency amount
 *
 * @author Acrobot
 */
public class CurrencyCheckEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    boolean outcome;

    private BigDecimal amount;
    private UUID account;
    private World world;

    public CurrencyCheckEvent(BigDecimal amount, UUID account, World world) {
        this.amount = amount;
        this.account = account;
        this.world = world;
    }

    public CurrencyCheckEvent(BigDecimal amount, Player player) {
        this(amount, player.getUniqueId(), player.getWorld());
    }

    /**
     * @return Does the account have enough currency available?
     */
    public boolean hasEnough() {
        return outcome;
    }

    /**
     * Sets if the account holds enough currency
     *
     * @param outcome Outcome of the currency check
     */
    public void hasEnough(boolean outcome) {
        this.outcome = outcome;
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
     * Sets the amount of currency transferred
     *
     * @param amount Amount to transfer
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Sets the amount of currency transferred
     *
     * @param amount Amount to transfer
     * @deprecated Use {@link #setAmount(java.math.BigDecimal)} if possible
     */
    public void setAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
    }

    /**
     * @return The world in which the transaction occurs
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
