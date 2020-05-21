package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Checks the amount of currency available
 *
 * @author Acrobot
 */
public class CurrencyAmountEvent extends EconomicEvent {
    private static final HandlerList handlers = new HandlerList();

    private BigDecimal amount = BigDecimal.ZERO;
    private UUID account;
    private World world;

    public CurrencyAmountEvent(UUID account, World world) {
        this.account = account;
        this.world = world;
    }

    public CurrencyAmountEvent(Player player) {
        this(player.getUniqueId(), player.getWorld());
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
     * Sets the amount of currency
     *
     * @param amount Amount available
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Sets the amount of currency
     *
     * @param amount Amount available
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

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
