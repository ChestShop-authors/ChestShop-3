package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.StockUpdateEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getHolder;
import static com.Acrobot.ChestShop.Listeners.Modules.StockCounterModule.fireStockUpdateEvents;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onItemMove(InventoryMoveItemEvent event) {
        InventoryHolder destinationHolder = getHolder(event.getDestination(), false);

        if (!Properties.TURN_OFF_HOPPER_PROTECTION && !(destinationHolder instanceof BlockState)) {
            InventoryHolder sourceHolder = getHolder(event.getSource(), false);
            if (ChestShopSign.isShopBlock(sourceHolder)) {
                event.setCancelled(true);
                return;
            }
        }

        if (StockUpdateEvent.hasHandlers() && ChestShopSign.isShopBlock(destinationHolder)) {
            Bukkit.getScheduler().runTask(ChestShop.getPlugin(), () ->
                    fireStockUpdateEvents(event.getDestination()));
        }
    }


}
