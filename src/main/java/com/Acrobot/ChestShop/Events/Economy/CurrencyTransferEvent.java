package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a transaction of goods between two entities
 *
 * @author Acrobot
 */
public class CurrencyTransferEvent extends EconomicEvent {
    private static final HandlerList handlers = new HandlerList();

    private BigDecimal amountSent;
    private BigDecimal amountReceived;

    private final Player initiator;

    private UUID partner;

    private Direction direction;

    public CurrencyTransferEvent(BigDecimal amount, Player initiator, UUID partner, Direction direction) {
        this(amount, amount, initiator, partner, direction);
    }

    public CurrencyTransferEvent(BigDecimal amountSent, BigDecimal amountReceived, Player initiator, UUID partner, Direction direction) {
        this.amountSent = amountSent;
        this.amountReceived = amountReceived;
        this.initiator = initiator;

        this.partner = partner;
        this.direction = direction;
    }

    /**
     * @deprecated Use {{@link #CurrencyTransferEvent(BigDecimal, Player, UUID, Direction)}
     */
    @Deprecated
    public CurrencyTransferEvent(double amount, Player initiator, UUID partner, Direction direction) {
        this(BigDecimal.valueOf(amount), initiator, partner, direction);
    }

    /**
     * @return Amount of currency sent
     * @deprecated Use {@link #getAmountSent()} and {@link #getAmountReceived()}
     */
    @Deprecated
    public BigDecimal getAmount() {
        return amountSent;
    }

    /**
     * @return Amount of currency, as a double
     * @deprecated Use {@link #getAmount()} if possible
     */
    @Deprecated
    public double getDoubleAmount() {
        return getAmount().doubleValue();
    }

    /**
     * Sets the amount of currency transferred
     *
     * @param amount Amount to transfer
     * @deprecated Use {@link #setAmountSent(BigDecimal)} and {@link #setAmountReceived(BigDecimal)}
     */
    @Deprecated
    public void setAmount(BigDecimal amount) {
        this.amountSent = amount;
        this.amountReceived = amount;
    }

    /**
     * Sets the amount of currency transferred
     *
     * @param amount Amount to transfer
     * @deprecated Use {@link #setAmount(java.math.BigDecimal)} if possible
     */
    @Deprecated
    public void setAmount(double amount) {
        setAmount(BigDecimal.valueOf(amount));
    }

    /**
     * Get the amount sent (subtracted from the sender account)
     *
     * @return The amount that got sent
     */
    public BigDecimal getAmountSent() {
        return amountSent;
    }

    /**
     * Set the amount sent (subtracted from the sender account)
     *
     * @param amountSent The amount that got sent
     */
    public void setAmountSent(BigDecimal amountSent) {
        this.amountSent = amountSent;
    }

    /**
     * Get the amount received (added from the receiver account)
     *
     * @return The amount that gets received
     */
    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    /**
     * Set the amount received (added from the receiver account)
     *
     * @param amountReceived The amount that gets received
     */
    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    /**
     * @return If the currency has been successfully transferred
     * @deprecated Use {@link #wasHandled()}
     */
    @Deprecated
    public boolean hasBeenTransferred() {
        return wasHandled();
    }

    /**
     * Sets the transaction's outcome
     *
     * @param success If the currency has been successfully transferred
     * @deprecated Use {@link #setHandled(boolean)}
     */
    @Deprecated
    public void setTransferred(boolean success) {
        setHandled(success);
    }

    /**
     * @return the direction that the money is transacted
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Get the player who initiated this transaction
     *
     * @return The player who initiated this transaction
     */
    public Player getInitiator() {
        return initiator;
    }

    /**
     * @return the partner of this transaction
     */
    public UUID getPartner() {
        return partner;
    }

    /**
     * @return The world in which the transaction occurs
     */
    public World getWorld() {
        return initiator.getWorld();
    }

    /**
     * @return Sender of the money
     */
    public UUID getSender() {
        return direction == Direction.PARTNER ? initiator.getUniqueId() : partner;
    }

    /**
     * @return Receiver of the money
     */
    public UUID getReceiver() {
        return direction == Direction.PARTNER ? partner : initiator.getUniqueId();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Direction {
        PARTNER,
        INITIATOR;
    }
}
