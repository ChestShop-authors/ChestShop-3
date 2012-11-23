package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Acrobot
 */
public class BlockPlace implements Listener {
    @EventHandler(ignoreCancelled = true)
    public static void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockAgainst();

        if (BlockUtil.isSign(block) && ChestShopSign.isValid((Sign) block.getState())) {
            event.setCancelled(true);
            return;
        }

        Block placed = event.getBlockPlaced();

        if (placed.getType() != Material.CHEST) {
            return;
        }


        Player player = event.getPlayer();

        if (Permission.has(player, Permission.ADMIN)) {
            return;
        }

        if (!Security.canAccess(player, placed)) {
            event.getPlayer().sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            event.setCancelled(true);
        }

        Chest neighbor = uBlock.findNeighbor(placed);

        if (neighbor == null) {
            return;
        }

        if (!Security.canAccess(event.getPlayer(), neighbor.getBlock())) {
            event.getPlayer().sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            event.setCancelled(true);
        }

    }
}
