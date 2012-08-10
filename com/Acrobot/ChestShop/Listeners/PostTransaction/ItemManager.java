package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

/**
 * @author Acrobot
 */
public class ItemManager implements Listener {
    @EventHandler
    public static void shopItemRemover(TransactionEvent event) {
        if (event.getTransactionType() != BUY) {
            return;
        }

        removeItems(event.getOwnerInventory(), event.getStock());
        addItems(event.getClientInventory(), event.getStock());

        event.getClient().updateInventory();
    }

    @EventHandler
    public static void inventoryItemRemover(TransactionEvent event) {
        if (event.getTransactionType() != SELL) {
            return;
        }

        removeItems(event.getClientInventory(), event.getStock());
        addItems(event.getOwnerInventory(), event.getStock());

        event.getClient().updateInventory();
    }

    private static void removeItems(Inventory inventory, ItemStack[] items) {
        for (ItemStack item : items) {
            InventoryUtil.remove(item, inventory);
        }
    }

    private static void addItems(Inventory inventory, ItemStack[] items) {
        for (ItemStack item : items) {
            inventory.addItem(item.clone());
        }
    }
}
