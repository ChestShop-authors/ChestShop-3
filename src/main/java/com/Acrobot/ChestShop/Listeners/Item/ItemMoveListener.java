package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler
    public static void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource() == null || !(event.getSource().getHolder() instanceof Chest)) {
            return;
        }

        if (!ChestShopSign.isShopChest(((BlockState) event.getSource().getHolder()).getBlock())) {
            return;
        }

        event.setCancelled(true);
    }
}
