package com.Acrobot.ChestShop.Listeners.Block.Break;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInteract;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static com.Acrobot.ChestShop.Config.Property.USE_BUILT_IN_PROTECTION;
import static org.bukkit.Material.CHEST;

/**
 * @author Acrobot
 */
public class ChestBreak implements Listener {
    @EventHandler(ignoreCancelled = true)
    public static void onChestBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != CHEST || !Config.getBoolean(USE_BUILT_IN_PROTECTION)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!PlayerInteract.canOpenOtherShops(player) && !ChestShop.canAccess(player, block)) {
            event.setCancelled(true);
        }
    }
}
