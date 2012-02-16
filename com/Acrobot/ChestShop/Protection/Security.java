package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Listeners.blockBreak;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author Acrobot
 */
public class Security {
    private static BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    public static ArrayList<Protection> protections = new ArrayList<Protection>();

    public static boolean protect(String name, Block block) {
        boolean works = false;
        for (int i = 0; i < protections.size() && !works; i++) works = protections.get(i).protect(name, block);
        return works;
    }

    public static boolean canAccess(Player player, Block block) {
        boolean works = true;
        for (int i = 0; i < protections.size() && works; i++) works = protections.get(i).canAccess(player, block);
        return works;
    }

    public static boolean isProtected(Block block) {
        boolean isProt = false;
        for (int i = 0; i < protections.size() && !isProt; i++) isProt = protections.get(i).isProtected(block);
        return isProt;
    }

    public static boolean canPlaceSign(Player p, Sign sign) {
        return !thereIsAnotherSignByPlayer(blockBreak.getAttachedFace(sign), sign.getBlock(), uLongName.stripName(p.getName()));
    }

    private static boolean thereIsAnotherSignByPlayer(Block baseBlock, Block signBlock, String shortName) {
        for (BlockFace bf : faces) {
            Block block = baseBlock.getRelative(bf);
            if (uSign.isSign(block) && uSign.isValid((Sign) block.getState()) && !block.equals(signBlock) && blockBreak.getAttachedFace((Sign) block.getState()).equals(baseBlock) && !((Sign) block.getState()).getLine(0).equals(shortName))
                return true;
        }
        return false;
    }
}
