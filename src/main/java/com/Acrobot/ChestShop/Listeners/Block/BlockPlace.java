package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Acrobot
 */
public class BlockPlace implements Listener {

    @EventHandler(ignoreCancelled = true)
    public static void onContainerPlace(BlockPlaceEvent event) {
        Block placed = event.getBlockPlaced();

        if (!uBlock.couldBeShopContainer(placed)) {
            return;
        }

        Player player = event.getPlayer();

        if (Permission.has(player, Permission.ADMIN)) {
            return;
        }

        if (!Security.canAccess(player, placed)) {
            Messages.ACCESS_DENIED.sendWithPrefix(event.getPlayer());
            event.setCancelled(true);
        }

        Block neighbor = uBlock.findNeighbor(placed);

        if (neighbor != null && !Security.canAccess(event.getPlayer(), neighbor)) {
            Messages.ACCESS_DENIED.sendWithPrefix(event.getPlayer());
            event.setCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public static void onPlaceAgainstSign(BlockPlaceEvent event) {
        Block against = event.getBlockAgainst();

        if (!ChestShopSign.isValid(against)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public static void onHopperDropperPlace(BlockPlaceEvent event) {
        Block placed = event.getBlockPlaced();

        if (placed.getType() != Material.HOPPER && placed.getType() != Material.DROPPER) {
            return;
        }

        List<BlockFace> searchDirections = new ArrayList<>();
        switch (placed.getType()) {
            case HOPPER:
                searchDirections.add(BlockFace.UP);
                searchDirections.add(((Directional) placed.getBlockData()).getFacing());
                break;
            case DROPPER:
                searchDirections.add(((Directional) placed.getBlockData()).getFacing());
                break;
        }

        for (BlockFace face : searchDirections) {
            Block relative = placed.getRelative(face);

            if (!uBlock.couldBeShopContainer(relative)) {
                continue;
            }

            if (!Security.canAccess(event.getPlayer(), relative)) {
                Messages.ACCESS_DENIED.sendWithPrefix(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }
    }
}
