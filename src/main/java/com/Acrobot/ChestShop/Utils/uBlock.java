package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Acrobot
 */
public class uBlock {
    public static final BlockFace[] CHEST_EXTENSION_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    public static final BlockFace[] SHOP_FACES = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    @Deprecated
    public static final BlockFace[] NEIGHBOR_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Sign getConnectedSign(BlockState blockState) {
        return getConnectedSign(blockState.getBlock());
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

    /**
     * @deprecated Use {@link #findConnectedContainer(Sign)}
     */
    @Deprecated
    public static org.bukkit.block.Chest findConnectedChest(Sign sign) {
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        return findConnectedChest(sign.getBlock(), signFace);
    }

    /**
     * @deprecated Use {@link #findConnectedContainer(Block)}
     */
    @Deprecated
    public static org.bukkit.block.Chest findConnectedChest(Block block) {
        BlockFace signFace = null;
        if (BlockUtil.isSign(block)) {
            BlockData data = block.getBlockData();
            if (data instanceof WallSign) {
                signFace = ((WallSign) data).getFacing().getOppositeFace();
            }
        }
        return findConnectedChest(block, signFace);
    }

    /**
     * @deprecated Use {@link #findConnectedContainer(Location, BlockFace)}
     */
    @Deprecated
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

    public static Container findConnectedContainer(Sign sign) {
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        return findConnectedContainer(sign.getLocation(), signFace);
    }

    public static Container findConnectedContainer(Block block) {
        BlockFace signFace = null;
        BlockData data = block.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        return findConnectedContainer(block.getLocation(), signFace);
    }

    private static Container findConnectedContainer(Location location, BlockFace signFace) {
        if (signFace != null) {
            Block faceBlock = location.clone().add(signFace.getModX(), signFace.getModY(), signFace.getModZ()).getBlock();
            if (uBlock.couldBeShopContainer(faceBlock)) {
                return (Container) faceBlock.getState();
            }
        }

        for (BlockFace bf : SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = location.clone().add(bf.getModX(), bf.getModY(), bf.getModZ()).getBlock();
                if (uBlock.couldBeShopContainer(faceBlock)) {
                    return (Container) faceBlock.getState();
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

    public static List<Sign> findConnectedShopSigns(InventoryHolder chestShopInventoryHolder) {
        List<Sign> result = new ArrayList<>();

        if (chestShopInventoryHolder instanceof DoubleChest) {
            BlockState leftChestSide = (BlockState) ((DoubleChest) chestShopInventoryHolder).getLeftSide();
            BlockState rightChestSide = (BlockState) ((DoubleChest) chestShopInventoryHolder).getRightSide();

            if (leftChestSide == null || rightChestSide == null) {
                return result;
            }

            Block leftChest = leftChestSide.getBlock();
            Block rightChest = rightChestSide.getBlock();

            if (ChestShopSign.isShopBlock(leftChest)) {
                result.addAll(uBlock.findConnectedShopSigns(leftChest));
            }

            if (ChestShopSign.isShopBlock(rightChest)) {
                result.addAll(uBlock.findConnectedShopSigns(rightChest));
            }
        }

        else if (chestShopInventoryHolder instanceof BlockState) {
            Block chestBlock = ((BlockState) chestShopInventoryHolder).getBlock();

            if (ChestShopSign.isShopBlock(chestBlock)) {
                result.addAll(uBlock.findConnectedShopSigns(chestBlock));
            }
        }

        return result;
    }

    public static List<Sign> findConnectedShopSigns(Block chestBlock) {
        List<Sign> result = new ArrayList<>();

        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = chestBlock.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            Container signContainer = findConnectedContainer(sign);
            if (!chestBlock.equals(signContainer.getBlock())) {
                continue;
            }

            if (ChestShopSign.isValid(sign)) {
                result.add(sign);
            }
        }

        return result;
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

    public static boolean couldBeShopContainer(Block block) {
        return block != null && Properties.SHOP_CONTAINERS.contains(block.getType());
    }

    public static boolean couldBeShopContainer(InventoryHolder holder) {
        return holder instanceof Container && couldBeShopContainer(((Container) holder).getBlock());
    }
}
