package com.Acrobot.ChestShop.Listeners.Block.Break;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInteract;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Acrobot
 */
public class ChestBreak implements Listener {
    @EventHandler(ignoreCancelled = true)
    public static void onChestBreak(BlockBreakEvent event) {
        if (!canChestBeBroken(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onExplosion(EntityExplodeEvent event) {
        if (event.blockList() == null || !Properties.USE_BUILT_IN_PROTECTION) {
            return;
        }

        for (Block block : event.blockList()) {
            if (!canChestBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private static boolean canChestBeBroken(Block chest, Player breaker) {
        if (!BlockUtil.isChest(chest) || !Properties.USE_BUILT_IN_PROTECTION || !ChestShopSign.isShopChest(chest)) {
            return true;
        }

        return breaker != null && (PlayerInteract.canOpenOtherShops(breaker) || ChestShop.canAccess(breaker, chest));
    }
}
