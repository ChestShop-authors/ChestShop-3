package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Listeners.blockBreak;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author Acrobot
 */
public class Security {
    private static final BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    private static final BlockFace[] blockFaces = {BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
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
        return !anotherShopFound(blockBreak.getAttachedFace(sign), sign.getBlock(), p) && canBePlaced(p, sign.getBlock());
    }

    private static boolean canBePlaced(Player p, Block signBlock) {
        for (BlockFace bf : blockFaces) {
            Block block = signBlock.getRelative(bf);

            if (block.getType() != Material.CHEST) continue;
            if (isProtected(block) && !canAccess(p, block)) return false;
        }

        return true;
    }

    private static boolean anotherShopFound(Block baseBlock, Block signBlock, Player p) {
        String shortName = uLongName.stripName(p.getName());
        if (Config.getBoolean(Property.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK)) return false;

        for (BlockFace bf : faces) {
            Block block = baseBlock.getRelative(bf);

            if (!uSign.isSign(block)) continue;

            Sign s = (Sign) block.getState();
            if (uSign.isValid(s) && !block.equals(signBlock) && blockBreak.getAttachedFace(s).equals(baseBlock) && !s.getLine(0).equals(shortName))
                return true;
        }
        return false;
    }
}
