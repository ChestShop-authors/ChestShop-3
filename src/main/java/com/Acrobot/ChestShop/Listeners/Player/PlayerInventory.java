package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * @author Acrobot
 */
public class PlayerInventory implements Listener {
    @EventHandler
    public static void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (!Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            return;
        }

        HumanEntity entity = event.getPlayer();

        if (!(entity instanceof Player) || (!(event.getInventory().getHolder() instanceof Chest) && !(event.getInventory().getHolder() instanceof DoubleChest))) {
            return;
        }

        Player player = (Player) entity;
        Block chest;

        if (event.getInventory().getHolder() instanceof Chest) {
            chest = ((BlockState) event.getInventory().getHolder()).getBlock();
        } else {
            chest = ((DoubleChest) event.getInventory().getHolder()).getLocation().getBlock();
        }

        if (!ChestShop.canAccess(player, chest)) {
            player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            event.setCancelled(true);
        }
    }
}
