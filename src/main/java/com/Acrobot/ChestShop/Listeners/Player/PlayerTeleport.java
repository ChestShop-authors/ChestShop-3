package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * A fix for a CraftBukkit bug.
 *
 * @author Acrobot
 */
public class PlayerTeleport implements Listener {

    @EventHandler
    public static void onPlayerTeleport(PlayerTeleportEvent event) {
        InventoryHolder holder = event.getPlayer().getOpenInventory().getTopInventory().getHolder();
        if (!(holder instanceof BlockState)) {
            return;
        }

        Block container;
        if (holder instanceof DoubleChest) {
            container = ((DoubleChest) holder).getLocation().getBlock();
        } else {
            container = ((BlockState) holder).getBlock();
        }

        if (ChestShopSign.isShopBlock(container)) {
            event.getPlayer().closeInventory();
        }
    }
}
