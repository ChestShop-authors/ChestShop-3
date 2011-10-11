package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Listeners.blockBreak;
import com.Acrobot.ChestShop.Protection.Plugins.Default;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Security {
    private static BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    public static Protection protection = new Default();

    public static boolean protect(String name, Block block) {
        return protection.protect(name, block);
    }

    public static boolean canAccess(Player player, Block block) {
        return protection.canAccess(player, block);
    }

    public static boolean isProtected(Block block) {
        return protection.isProtected(block);
    }

    public static boolean canPlaceSign(Player p, Sign sign) {
        Block block = blockBreak.getAttachedFace(sign);
        return !thereIsAnotherSignByPlayer(block, sign.getBlock(), uLongName.stripName(p.getName()));
    }

    private static boolean thereIsAnotherSignByPlayer(Block baseBlock, Block signBlock, String shortName){
        for (BlockFace bf : faces){
            Block block = baseBlock.getRelative(bf);
            if(uSign.isSign(block) && !block.equals(signBlock) && blockBreak.getAttachedFace((Sign) block.getState()).equals(baseBlock) && !((Sign) block.getState()).getLine(0).equals(shortName)) return true;
        }
        return false;
    }
}
