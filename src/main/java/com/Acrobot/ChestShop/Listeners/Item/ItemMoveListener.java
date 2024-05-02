package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Listeners.Modules.StockCounterModule;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getHolder;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onItemMove(InventoryMoveItemEvent event) {
        InventoryHolder destinationHolder = getHolder(event.getDestination(), false);
        InventoryHolder sourceHolder = getHolder(event.getSource(), false);

        if (!(destinationHolder instanceof BlockState) && ChestShopSign.isShopBlock(sourceHolder)) {
            event.setCancelled(true);
        } else if (Properties.USE_STOCK_COUNTER && ChestShopSign.isShopBlock(destinationHolder) && sourceHolder instanceof Hopper) {
            StockCounterModule.updateCounterOnItemMoveEvent(event.getItem(), destinationHolder);
        }
    }


}
