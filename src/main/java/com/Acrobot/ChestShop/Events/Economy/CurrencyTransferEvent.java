package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a transaction of goods between two entities
 *
 * @author Acrobot
 */
public class CurrencyTransferEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private BigDecimal amount;
    private World world;
    private UUID sender;
    private UUID receiver;
    private boolean success;

    public CurrencyTransferEvent(BigDecimal amount, UUID sender, UUID receiver, World world) {
        this.amount = amount;
        this.world = world;

        this.sender = sender;
        this.receiver = receiver;
    }

    public CurrencyTransferEvent(double amount, UUID sender, UUID receiver, World world) {
        this(BigDecimal.valueOf(amount), sender, receiver, world);
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
     * @return If the currency has been successfully transferred
     */
    public boolean hasBeenTransferred() {
        return success;
    }

    /**
     * Sets the transaction's outcome
     *
     * @param success If the currency has been successfully transferred
     */
    public void setTransferred(boolean success) {
        this.success = success;
    }

    /**
     * @return The world in which the transaction occurs
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return Sender of the money
     */
    public UUID getSender() {
        return sender;
    }

    /**
     * @return Receiver of the money
     */
    public UUID getReceiver() {
        return receiver;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
