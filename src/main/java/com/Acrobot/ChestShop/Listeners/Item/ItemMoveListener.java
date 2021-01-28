package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getHolder;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource() == null || getHolder(event.getDestination(), false) instanceof BlockState) {
            return;
        }

        if (!ChestShopSign.isShopBlock(getHolder(event.getSource(), false))) {
            return;
        }

        event.setCancelled(true);
    }
}
