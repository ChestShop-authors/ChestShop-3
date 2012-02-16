package com.Acrobot.ChestShop.Listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Acrobot
 */
public class entityExplode implements Listener {
    @EventHandler
    public static void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled() || event.blockList() == null) return;
        for (Block block : event.blockList()) {
            if (blockBreak.cancellingBlockBreak(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
