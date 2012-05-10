package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Signs.restrictedSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.material.Attachable;

/**
 * @author Acrobot
 */
public class uBlock {
    private static final BlockFace[] chestFaces = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    private static final BlockFace[] shopFaces = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Chest findConnectedChest(Sign sign) {
        Block block = sign.getBlock();
        return findConnectedChest(block);
    }

    public static Chest findConnectedChest(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);
            if (faceBlock.getType() == Material.CHEST) {
                return (Chest) faceBlock.getState();
            }
        }
        return null;
    }

    public static Sign findValidShopSign(Block block, String originalName) {
        Sign ownerShopSign = null;

        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);

            if (!uSign.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (uSign.isValid(sign) && signIsAttachedToBlock(sign, block)) {
                if (!sign.getLine(0).equals(originalName)) {
                    return sign;
                } else if (ownerShopSign == null) {
                    ownerShopSign = sign;
                }
            }
        }

        return ownerShopSign;
    }

    public static Sign findAnyNearbyShopSign(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);

            if (!uSign.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (uSign.isValid(sign)) {
                return sign;
            }
        }
        return null;
    }

    public static Sign findRestrictedSign(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);

            if (!uSign.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (restrictedSign.isRestricted(sign) && signIsAttachedToBlock(sign, block)) {
                return sign;
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

    private static boolean signIsAttachedToBlock(Sign sign, Block block) {
        return sign.getBlock().equals(block) || getAttachedFace(sign).equals(block);
    }

    public static Block getAttachedFace(Sign sign) {
        return sign.getBlock().getRelative(((Attachable) sign.getData()).getAttachedFace());
    }
}
