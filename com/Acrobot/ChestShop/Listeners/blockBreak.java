package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Utils.SearchForBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 * @author Acrobot
 */
public class blockBreak extends BlockListener {
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        boolean isAdmin = Permission.has(player, Permission.ADMIN);

        if (isAdmin) {
            return;
        }

        Sign sign = SearchForBlock.findSign(block);

        if (sign != null) {
            if (!player.getName().startsWith(sign.getLine(0))) {
                event.setCancelled(true);
            }
        }
    }
}
