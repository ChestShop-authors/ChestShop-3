package com.Acrobot.ChestShop.Listeners.Shop.PostTransaction;

import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

/**
 * @author Acrobot
 */
public class EconomicModule implements Listener {
    @EventHandler
    public static void onBuyTransaction(TransactionEvent event) {
        if (event.getTransactionType() != BUY) {
            return;
        }

        Economy.subtract(event.getClient().getName(), event.getPrice());
        Economy.add(event.getOwner().getName(), event.getPrice());
    }

    @EventHandler
    public static void onSellTransaction(TransactionEvent event) {
        if (event.getTransactionType() != SELL) {
            return;
        }

        Economy.subtract(event.getOwner().getName(), event.getPrice());
        Economy.add(event.getClient().getName(), event.getPrice());
    }
}
