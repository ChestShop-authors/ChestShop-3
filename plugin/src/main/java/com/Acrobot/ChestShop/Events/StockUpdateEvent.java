package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a state after a shop's stock changes
 *
 * @author bjorn-out
 */
public class StockUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final int stock;
    private final Sign sign;

    public StockUpdateEvent(int stock, Sign sign) {
        this.stock = stock;
        this.sign = sign;
    }

    /**
     * @return Stock available (number of items)
     */
    public int getStock() {
        return stock;
    }

    /**
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static boolean hasHandlers() {
        return handlers.getRegisteredListeners().length > 0;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}
