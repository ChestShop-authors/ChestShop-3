package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Acrobot
 */
public class EntityExplode implements Listener {
    @EventHandler(ignoreCancelled = true)
    public static void onEntityExplode(EntityExplodeEvent event) {
        if (event.blockList() == null || !Config.getBoolean(Property.USE_BUILT_IN_PROTECTION)) {
            return;
        }

        for (Block block : event.blockList()) {
            if (BlockBreak.cancellingBlockBreak(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
