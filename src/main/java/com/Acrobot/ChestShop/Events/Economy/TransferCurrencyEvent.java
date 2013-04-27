package com.Acrobot.ChestShop.Events.Economy;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

/**
 * @author Acrobot
 */
public class TransferCurrencyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private BigDecimal amount;

    public TransferCurrencyEvent(BigDecimal amount, String fromWho, String toWho)

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
