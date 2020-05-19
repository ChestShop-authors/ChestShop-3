package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a subtraction of goods from entity
 * <p>
 * Use {@link CurrencyTransferEvent} if you want to transfer money from one account to another one!
 *
 * @author Acrobot
 */
public class CurrencySubtractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID target;
    private final World world;
    boolean subtracted = false;
    private BigDecimal amount;

    public CurrencySubtractEvent(BigDecimal amount, UUID target, World world) {
        this.amount = amount;
        this.target = target;
        this.world = world;
    }

    public CurrencySubtractEvent(BigDecimal amount, Player target) {
        this(amount, target.getUniqueId(), target.getWorld());
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return Amount of currency
     */
    public BigDecimal getAmount() {
        return amount;
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
     * @return Amount of currency, as a double
     * @deprecated Use {@link #getAmount()} if possible
     */
    public double getDoubleAmount() {
        return amount.doubleValue();
    }

    /**
     * @return Was the money already subtracted?
     */
    public boolean isSubtracted() {
        return subtracted;
    }

    /**
     * Set if the money was subtracted from the account
     *
     * @param subtracted Was the money subtracted?
     */
    public void setSubtracted(boolean subtracted) {
        this.subtracted = subtracted;
    }

    /**
     * @return The world in which the transaction occurs
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return Account from which the currency is subtracted
     */
    public UUID getTarget() {
        return target;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
