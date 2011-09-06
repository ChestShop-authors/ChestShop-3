package com.Acrobot.ChestShop.Listeners;

import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

/**
 * @author Acrobot
 */
public class entityExplode extends EntityListener {
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled() || event.blockList() == null) return;
        for (Block block : event.blockList()) {
            if (blockBreak.cancellingBlockBreak(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
