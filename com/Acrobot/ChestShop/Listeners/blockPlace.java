package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Utils.SignUtil;
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
        if (SignUtil.isSign(block) && SignUtil.isValid((Sign) block.getState())) {
            event.setCancelled(true);
        }
    }
}
