package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

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

        if (isOwnerEconomicalyActive(event)) {
            Economy.add(event.getOwner().getName(), event.getPrice());
        }

        Economy.subtract(event.getClient().getName(), event.getPrice());
    }

    @EventHandler
    public static void onSellTransaction(TransactionEvent event) {
        if (event.getTransactionType() != SELL) {
            return;
        }

        if (isOwnerEconomicalyActive(event)) {
            Economy.subtract(event.getOwner().getName(), event.getPrice());
        }

        Economy.add(event.getClient().getName(), event.getPrice());
    }

    public static String getServerAccountName() {
        return Config.getString(Property.SERVER_ECONOMY_ACCOUNT);
    }

    public static boolean isServerShop(Inventory inventory) {
        return inventory instanceof AdminInventory;
    }

    public static boolean isOwnerEconomicalyActive(TransactionEvent event) {
        return !isServerShop(event.getOwnerInventory()) || !getServerAccountName().isEmpty();
    }
}
