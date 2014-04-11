package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler
    public static void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource() == null || !BlockUtil.isChest(event.getSource().getHolder())) {
            return;
        }

        if (!ChestShopSign.isShopChest(event.getSource().getHolder())) {
            return;
        }

        event.setCancelled(true);
    }
}
