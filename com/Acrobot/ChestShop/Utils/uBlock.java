package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Signs.restrictedSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

/**
 * @author Acrobot
 */
public class uBlock {

    private static final BlockFace[] chestFaces = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    private static final BlockFace[] shopFaces = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Chest findChest(Sign sign) {
        Block block = sign.getBlock();
        return findChest(block);
    }

    public static Chest findChest(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);
            if (faceBlock.getType() == Material.CHEST) return (Chest) faceBlock.getState();
        }
        return null;
    }

    public static Sign findSign(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);
            if (uSign.isSign(faceBlock)) {
                Sign sign = (Sign) faceBlock.getState();
                if (uSign.isValid(sign)) return sign;
            }
        }
        return null;
    }

    public static Sign findRestrictedSign(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);
            if (uSign.isSign(faceBlock)) {
                Sign sign = (Sign) faceBlock.getState();
                if (restrictedSign.isRestricted(sign)) return sign;
            }
        }
        return null;
    }

    public static Chest findNeighbor(Block block) {
        for (BlockFace blockFace : chestFaces) {
            Block neighborBlock = block.getRelative(blockFace);
            if (neighborBlock.getType() == Material.CHEST) {
                return (Chest) neighborBlock.getState();
            }
        }
        return null;
    }

    public static Chest findNeighbor(Chest chest) {
        return findNeighbor(chest.getBlock());
    }
}
