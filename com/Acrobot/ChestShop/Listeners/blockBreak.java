package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.PistonBaseMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Acrobot
 */
public class blockBreak implements Listener {
    public static boolean cancellingBlockBreak(Block block, Player player) {
        if (block == null || !uSign.isSign(block)) block.getState().update(); return false;
        if (player != null && (Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD))) return false;

        if (restrictedSign(block)) return !restrictedSign.canDestroy(player, uBlock.findRestrictedSign(block));

        Sign sign = uBlock.findSign(block, (player != null ? uLongName.stripName(player.getName()) : null));
        if (!isCorrectSign(sign, block)) return false; //It's not a correct shop sign, so don't cancel it
        if (playerIsNotOwner(player, sign)) return true; //Player is not the owner of the shop - cancel!
        if (weShouldReturnMoney() && !Permission.has(player, Permission.NOFEE)){
            float refundPrice = Config.getFloat(Property.SHOP_REFUND_PRICE);
            Economy.add(uLongName.getName(sign.getLine(0)), refundPrice); //Add some money
            player.sendMessage(Config.getLocal(Language.SHOP_REFUNDED).replace("%amount", Economy.formatBalance(refundPrice)));
        }
        return false; //Player is the owner, so we don't want to cancel this :)
    }

    private static boolean weShouldReturnMoney() {
        //We should return money when it's turned on in config, obviously
        return Config.getFloat(Property.SHOP_REFUND_PRICE) != 0;
    }

    private static boolean restrictedSign(Block block) {
        return uBlock.findRestrictedSign(block) != null;
    }

    @EventHandler
    public static void onBlockBreak(BlockBreakEvent event) {
        if (cancellingBlockBreak(event.getBlock(), event.getPlayer())) event.setCancelled(true);
    }

    private static boolean isCorrectSign(Sign sign, Block block) {
        return sign != null && (sign.getBlock().equals(block) || getAttachedFace(sign).equals(block));
    }

    public static Block getAttachedFace(Sign sign) {
        return sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
    }

    private static boolean playerIsNotOwner(Player player, Sign sign) {
        return player == null || (!uLongName.stripName(player.getName()).equals(sign.getLine(0))
                && !Permission.otherName(player, sign.getLine(0)));
    }

    @EventHandler
    public static void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : getExtendBlocks(event)) {
            if (cancellingBlockBreak(b, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public static void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (cancellingBlockBreak(getRetractBlock(event), null)) event.setCancelled(true);
    }

    private static Block getRetractBlock(BlockPistonRetractEvent event) {
        Block block = getRetractLocationBlock(event);
        return (block != null && !uSign.isSign(block) ? block : null);
    }

    //Those are fixes for CraftBukkit's piston bug, where piston appears not to be a piston.
    private static BlockFace getPistonDirection(Block block) {
        return block.getState().getData() instanceof PistonBaseMaterial ? ((PistonBaseMaterial) block.getState().getData()).getFacing() : null;
    }

    private static Block getRetractLocationBlock(BlockPistonRetractEvent event) {
        BlockFace pistonDirection = getPistonDirection(event.getBlock());
        return pistonDirection != null ? event.getBlock().getRelative((pistonDirection), 2).getLocation().getBlock() : null;
    }

    private static List<Block> getExtendBlocks(BlockPistonExtendEvent event){
        BlockFace pistonDirection = getPistonDirection(event.getBlock());
        if (pistonDirection == null) return new ArrayList<Block>();
        Block piston = event.getBlock();
        ArrayList<Block> list = new ArrayList<Block>();
        for (int b = 1; b < event.getLength() + 1; b++){
            Block block = piston.getRelative(pistonDirection, b);
            Material blockType = block.getType();
            if (blockType == Material.AIR) break;
            list.add(block);
        }
        return list;
    }
}
