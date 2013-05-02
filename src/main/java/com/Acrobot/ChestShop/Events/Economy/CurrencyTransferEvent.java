package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * @author Acrobot
 */
public class CurrencyTransferEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private BigDecimal amount;
    private World world;
    private String sender;
    private String receiver;

    public CurrencyTransferEvent(BigDecimal amount, String sender, String receiver, World world) {
        this.amount = amount;
        this.world = world;

        this.sender = sender;
        this.receiver = receiver;
    }

    public CurrencyTransferEvent(double amount, String sender, String receiver, World world) {
        this(BigDecimal.valueOf(amount), sender, receiver, world);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public double getDoubleAmount() {
        return amount.doubleValue();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount);
    }

    public World getWorld() {
        return world;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
