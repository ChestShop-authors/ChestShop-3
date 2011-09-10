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
        if (player != null && (Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD))) return false;

        if (uSign.isSign(block)) block.getState().update();

        Sign sign = uBlock.findRestrictedSign(block);
        if (isCorrectSign(sign, block)) return true;

        sign = uBlock.findSign(block);
        return isCorrectSign(sign, block) && playerIsNotOwner(player, sign);
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (cancellingBlockBreak(event.getBlock(), event.getPlayer())) event.setCancelled(true);
    }

    private static boolean isCorrectSign(Sign sign, Block block) {
        return sign != null && (sign.getBlock() == block || getAttachedFace(sign) == block);
    }

    private static Block getAttachedFace(Sign sign) {
        return sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
    }

    private static boolean playerIsNotOwner(Player player, Sign sign) {
        return player == null || !uLongName.stripName(player.getName()).equals(sign.getLine(0));
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
        try{
            if (!uSign.isSign(event.getRetractLocation().getBlock()) && cancellingBlockBreak(event.getRetractLocation().getBlock(), null)) event.setCancelled(true);
        } catch (Exception ignored){}
    }
}
