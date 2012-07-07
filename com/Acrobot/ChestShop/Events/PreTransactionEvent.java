package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Shop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Acrobot
 */
public class PreTransactionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Shop shop;
    private final Player player;
    private final TransactionEvent.Type transactionType;

    private boolean isCancelled = false;

    public PreTransactionEvent(Shop shop, Player player, TransactionEvent.Type type) {
        this.shop = shop;
        this.player = player;
        this.transactionType = type;
    }

    public Shop getShop() {
        return shop;
    }

    public Player getPlayer() {
        return player;
    }

    public TransactionEvent.Type getTransactionType() {
        return transactionType;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
