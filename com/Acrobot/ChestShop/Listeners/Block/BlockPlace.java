package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
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

        if (placed.getType() == Material.CHEST) {
            Chest neighbor = uBlock.findNeighbor(placed);
            if (neighbor == null) {
                return;
            }

            Block neighborBlock = neighbor.getBlock();
            if (!Security.canAccess(event.getPlayer(), neighborBlock)) {
                event.getPlayer().sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                event.setCancelled(true);
            }
        }
    }
}
