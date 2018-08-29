package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.material.Attachable;

/**
 * @author Acrobot
 */
public class uBlock {
    public static final BlockFace[] CHEST_EXTENSION_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    public static final BlockFace[] SHOP_FACES = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    @Deprecated
    public static final BlockFace[] NEIGHBOR_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Sign getConnectedSign(org.bukkit.block.Chest chest) {
        Sign sign = uBlock.findAnyNearbyShopSign(chest.getBlock());

        if (sign == null) {
            Block neighbor = findNeighbor(chest.getBlock());
            if (neighbor != null) {
                sign = uBlock.findAnyNearbyShopSign(neighbor);
            }
        }

        return sign;
    }

    public static Sign getConnectedSign(Block block) {
        Sign sign = uBlock.findAnyNearbyShopSign(block);

        if (sign == null) {
            Block neighbor = findNeighbor(block);
            if (neighbor != null) {
                sign = uBlock.findAnyNearbyShopSign(neighbor);
            }
        }

        return sign;
    }

    public static org.bukkit.block.Chest findConnectedChest(Sign sign) {
        BlockFace signFace = null;
        if (((org.bukkit.material.Sign) sign.getData()).isWallSign()) {
            signFace = ((Attachable) sign.getData()).getAttachedFace();
        }
        return findConnectedChest(sign.getBlock(), signFace);
    }

    public static org.bukkit.block.Chest findConnectedChest(Block block) {
        BlockFace signFace = null;
        if (BlockUtil.isSign(block)) {
            Sign sign = (Sign) block.getState();
            if (((org.bukkit.material.Sign) sign.getData()).isWallSign()) {
                signFace = ((Attachable) sign.getData()).getAttachedFace();
            }
        }
        return findConnectedChest(block, signFace);
    }

    private static org.bukkit.block.Chest findConnectedChest(Block block, BlockFace signFace) {
        if (signFace != null) {
            Block faceBlock = block.getRelative(signFace);
            if (BlockUtil.isChest(faceBlock)) {
                return (org.bukkit.block.Chest) faceBlock.getState();
            }
        }

        for (BlockFace bf : SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = block.getRelative(bf);
                if (BlockUtil.isChest(faceBlock)) {
                    return (org.bukkit.block.Chest) faceBlock.getState();
                }
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

    public static org.bukkit.block.Chest findNeighbor(org.bukkit.block.Chest chest) {
        Block neighbor = findNeighbor(chest.getBlock());
        return neighbor != null ? (org.bukkit.block.Chest) neighbor.getState() : null;
    }

    public static Block findNeighbor(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Chest)) {
            return null;
        }

        Chest chestData = (Chest) blockData;
        if (chestData.getType() == Chest.Type.SINGLE) {
            return null;
        }

        BlockFace chestFace = chestData.getFacing();
        // we have to rotate is to get the adjacent chest
        // west, right -> south
        // west, left -> north
        if (chestFace == BlockFace.WEST) {
            chestFace = BlockFace.NORTH;
        } else if (chestFace == BlockFace.NORTH) {
            chestFace = BlockFace.EAST;
        } else if (chestFace == BlockFace.EAST) {
            chestFace = BlockFace.SOUTH;
        } else if (chestFace == BlockFace.SOUTH) {
            chestFace = BlockFace.WEST;
        }
        if (chestData.getType() == Chest.Type.RIGHT) {
            chestFace = chestFace.getOppositeFace();
        }

        Block neighborBlock = block.getRelative(chestFace);
        if (neighborBlock.getType() == block.getType()) {
            return neighborBlock;
        }

        return null;
    }

    private static boolean signIsAttachedToBlock(Sign sign, Block block) {
        return sign.getBlock().equals(block) || BlockUtil.getAttachedBlock(sign).equals(block);
    }
}
