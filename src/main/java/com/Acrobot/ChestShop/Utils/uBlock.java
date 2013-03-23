package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

/**
 * @author Acrobot
 */
public class uBlock {
    public static final BlockFace[] CHEST_EXTENSION_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    public static final BlockFace[] SHOP_FACES = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    public static final BlockFace[] NEIGHBOR_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Sign getConnectedSign(Chest chest) {
        Sign sign = uBlock.findAnyNearbyShopSign(chest.getBlock());

        if (sign == null && getNeighbor(chest) != null) {
            sign = uBlock.findAnyNearbyShopSign(getNeighbor(chest).getBlock());
        }

        return sign;
    }

    private static Chest getNeighbor(Chest chest) {
        Block chestBlock = chest.getBlock();

        for (BlockFace chestFace : NEIGHBOR_FACES) {
            Block relative = chestBlock.getRelative(chestFace);

            if (BlockUtil.isChest(relative)) {
                return (Chest) relative.getState();
            }
        }

        return null;
    }

    public static Chest findConnectedChest(Sign sign) {
        Block block = sign.getBlock();
        return findConnectedChest(block);
    }

    public static Chest findConnectedChest(Block block) {
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);
            if (BlockUtil.isChest(faceBlock)) {
                return (Chest) faceBlock.getState();
            }
        }
        return null;
    }

    public static Sign findValidShopSign(Block block, String originalName) {
        Sign ownerShopSign = null;

        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (ChestShopSign.isValid(sign) && signIsAttachedToBlock(sign, block)) {
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
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (ChestShopSign.isValid(sign)) {
                return sign;
            }
        }
        return null;
    }

    public static Chest findNeighbor(Block block) {
        for (BlockFace blockFace : CHEST_EXTENSION_FACES) {
            Block neighborBlock = block.getRelative(blockFace);

            if (neighborBlock.getType() == block.getType()) {
                return (Chest) neighborBlock.getState();
            }
        }

        return null;
    }

    private static boolean signIsAttachedToBlock(Sign sign, Block block) {
        return sign.getBlock().equals(block) || BlockUtil.getAttachedFace(sign).equals(block);
    }
}
