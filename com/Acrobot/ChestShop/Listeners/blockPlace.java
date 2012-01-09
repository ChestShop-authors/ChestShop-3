package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Acrobot
 */
public class blockPlace extends BlockListener {
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockAgainst();
        if (uSign.isSign(block) && uSign.isValid((Sign) block.getState())) {
            event.setCancelled(true);
            return;
        }

        Block placed = event.getBlockPlaced();
        if (placed.getType() == Material.CHEST){
            Chest neighbor = uBlock.findNeighbor(placed);
            if (neighbor == null) return;

            Block neighborBlock = neighbor.getBlock();
            if (Security.isProtected(neighborBlock) && !Security.canAccess(event.getPlayer(), neighborBlock)){
                event.getPlayer().sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                event.setCancelled(true);
            }
        }
    }
}
