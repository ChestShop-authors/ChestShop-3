package com.Acrobot.ChestShop.Listeners.Transaction;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Containers.Container;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class EmptyShopDeleter implements Listener {
    @EventHandler
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() != TransactionEvent.Type.BUY) {
            return;
        }

        if (shopShouldBeRemoved(event.getContainer())) {
            event.getSign().getBlock().setType(Material.AIR);
            event.getContainer().addItem(new ItemStack(Material.SIGN, 1));
        }
    }

    private static boolean shopShouldBeRemoved(Container container) {
        return Config.getBoolean(Property.REMOVE_EMPTY_SHOPS) && shopIsEmpty(container);
    }

    private static boolean shopIsEmpty(Container container) {
        return container.isEmpty();
    }
}
