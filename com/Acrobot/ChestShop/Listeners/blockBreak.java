package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

/**
 * @author Acrobot
 */
public class blockBreak extends BlockListener {
    public static boolean cancellingBlockBreak(Block block, Player player) {
        if (player != null && Permission.has(player, Permission.ADMIN)) return false;

        if (uSign.isSign(block)) block.getState().update();

        Sign sign = uBlock.findRestrictedSign(block);
        if (sign != null) return true;

        sign = uBlock.findSign(block);
        return sign != null && (player == null || (!uLongName.stripName(player.getName()).equals(sign.getLine(0))));
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (cancellingBlockBreak(event.getBlock(), event.getPlayer())) event.setCancelled(true);
    }

    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks()) {
            if (cancellingBlockBreak(b, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (cancellingBlockBreak(event.getRetractLocation().getBlock(), null)) event.setCancelled(true);
    }
}
