package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Acrobot
 */
public class PlayerInventory implements Listener {
    @EventHandler
    public static void onInventoryOpen(InventoryOpenEvent event) {
        if (!Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            return;
        }

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BlockState)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Block container;

        if (holder instanceof DoubleChest) {
            container = ((DoubleChest) holder).getLocation().getBlock();
        } else {
            container = ((BlockState) holder).getBlock();
        }

        if (!ChestShopSign.isShopBlock(container)) {
            return;
        }

        if (!Security.canAccess(player, container)) {
            Messages.ACCESS_DENIED.sendWithPrefix(player);
            event.setCancelled(true);
        }
    }
}
