package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Block;
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
        }
    }
}
