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

import java.util.ArrayList;
import java.util.List;

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
        if (!(holder instanceof BlockState) && !(holder instanceof DoubleChest)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        List<Block> containers = new ArrayList<>();

        if (holder instanceof DoubleChest) {
            containers.add(((BlockState) ((DoubleChest) holder).getLeftSide()).getBlock());
            containers.add(((BlockState) ((DoubleChest) holder).getRightSide()).getBlock());
        } else {
            containers.add(((BlockState) holder).getBlock());
        }

        boolean canAccess = false;
        for (Block container : containers) {
            if (ChestShopSign.isShopBlock(container)) {
                if (Security.canAccess(player, container)) {
                    canAccess = true;
                }
            } else {
                canAccess = true;
            }
        }

        if (!canAccess) {
            Messages.ACCESS_DENIED.sendWithPrefix(player);
            event.setCancelled(true);
        }
    }
}
