package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.Directional;
import org.bukkit.material.PistonBaseMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Acrobot
 */
public class BlockBreak implements Listener {
    public static boolean cancellingBlockBreak(Block block, Player player) {
        if (block == null) return false;

        if (uSign.isSign(block)) block.getState().update(); //Show the text immediately

        if (restrictedSign(block)) return !restrictedSign.canDestroy(player, uBlock.findRestrictedSign(block));

        Sign sign = uBlock.findValidShopSign(block, (player != null ? uName.stripName(player.getName()) : null));
        if (!isCorrectSign(sign, block)) return false; //It's not a correct shop sign, so don't cancel it
        if (playerIsNotOwner(player, sign)) return !isAdmin(player); //If the player isn't the owner or an admin - cancel!

        if (weShouldReturnMoney() && !Permission.has(player, Permission.NOFEE)) {
            float refundPrice = Config.getFloat(Property.SHOP_REFUND_PRICE);
            Economy.add(uName.getName(sign.getLine(0)), refundPrice); //Add some money
            player.sendMessage(Config.getLocal(Language.SHOP_REFUNDED).replace("%amount", Economy.formatBalance(refundPrice)));
        }

        return false; //Player is the owner, so we don't want to cancel this :)
    }

    private static boolean isAdmin(Player p) {
        return p != null && (Permission.has(p, Permission.ADMIN) || Permission.has(p, Permission.MOD));
    }

    private static boolean weShouldReturnMoney() {
        //We should return money when it's turned on in config, obviously
        return Config.getFloat(Property.SHOP_REFUND_PRICE) != 0;
    }

    private static boolean restrictedSign(Block block) {
        return uBlock.findRestrictedSign(block) != null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockBreakEvent event) {
        if (cancellingBlockBreak(event.getBlock(), event.getPlayer())) event.setCancelled(true);
    }

    private static boolean isCorrectSign(Sign sign, Block block) {
        return sign != null && (sign.getBlock().equals(block) || uBlock.getAttachedFace(sign).equals(block));
    }

    private static boolean playerIsNotOwner(Player player, Sign sign) {
        return player == null || (!uName.stripName(player.getName()).equals(sign.getLine(0))
                && !Permission.otherName(player, sign.getLine(0)));
    }

    @EventHandler(ignoreCancelled = true)
    public static void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : getExtendBlocks(event)) {
            if (cancellingBlockBreak(b, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (cancellingBlockBreak(getRetractBlock(event), null)) event.setCancelled(true);
    }

    private static Block getRetractBlock(BlockPistonRetractEvent event) {
        Block block = getRetractLocationBlock(event);
        return (block != null && !uSign.isSign(block) ? block : null);
    }

    //Those are fixes for CraftBukkit's piston bug, where piston appears not to be a piston.
    private static BlockFace getPistonDirection(Block block) {
        return block.getState().getData() instanceof PistonBaseMaterial ? ((Directional) block.getState().getData()).getFacing() : null;
    }

    private static Block getRetractLocationBlock(BlockPistonRetractEvent event) {
        BlockFace pistonDirection = getPistonDirection(event.getBlock());
        return pistonDirection != null ? event.getBlock().getRelative((pistonDirection), 2).getLocation().getBlock() : null;
    }

    private static List<Block> getExtendBlocks(BlockPistonExtendEvent event) {
        BlockFace pistonDirection = getPistonDirection(event.getBlock());

        if (pistonDirection == null) {
            return new ArrayList<Block>();
        }

        Block piston = event.getBlock();
        List<Block> pushedBlocks = new ArrayList<Block>();

        for (int currentBlock = 1; currentBlock < event.getLength() + 1; currentBlock++) {
            Block block = piston.getRelative(pistonDirection, currentBlock);
            Material blockType = block.getType();

            if (blockType == Material.AIR) {
                break;
            }

            pushedBlocks.add(block);
        }

        return pushedBlocks;
    }
}
