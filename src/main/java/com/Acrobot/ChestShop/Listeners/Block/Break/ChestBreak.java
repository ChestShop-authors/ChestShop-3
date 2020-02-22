package com.Acrobot.ChestShop.Listeners.Block.Break;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Acrobot
 */
public class ChestBreak implements Listener {
    @EventHandler(ignoreCancelled = true)
    public static void onChestBreak(BlockBreakEvent event) {
        if (!canBeBroken(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onExplosion(EntityExplodeEvent event) {
        if (event.blockList() == null || !Properties.USE_BUILT_IN_PROTECTION) {
            return;
        }

        for (Block block : event.blockList()) {
            if (!canBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!canBeBroken(event.getBlock(), null)) {
            event.setCancelled(true);
        }
    }

    private static boolean canBeBroken(Block block, Player breaker) {
        if (!uBlock.couldBeShopContainer(block) || !Properties.USE_BUILT_IN_PROTECTION) {
            return true;
        }

        Sign shopSign = uBlock.findAnyNearbyShopSign(block);
        if (breaker != null) {
            return  ChestShopSign.hasPermission(breaker, Permission.OTHER_NAME_DESTROY, shopSign);
        }
        return shopSign == null;
    }
}
