package com.Acrobot.Breeze.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Acrobot
 */
public class BlockUtil {
    /**
     * Checks if the block is a sign
     *
     * @param block Block to check
     * @return Is this block a sign?
     */
    public static boolean isSign(Block block) {
        return block.getType() == Material.SIGN
                || block.getType() == Material.WALL_SIGN;
    }

    /**
     * Checks if the block is a chest
     *
     * @param block Block to check
     * @return Is this block a chest?
     */
    public static boolean isChest(Block block) {
        return block.getState() instanceof Chest;
    }

    /**
     * Checks if the InventoryHolder is a chest
     *
     * @param holder Inventory holder to check
     * @return Is this holder a chest?
     */
    public static boolean isChest(InventoryHolder holder) {
        return holder instanceof Chest || holder instanceof DoubleChest;
    }

    /**
     * Gets the block to which the sign is attached
     *
     * @param sign Sign which is attached
     * @return Block to which the sign is attached
     */
    public static Block getAttachedBlock(org.bukkit.block.Sign sign) {
        BlockFace direction;
        BlockData blockData = sign.getBlockData();
        if (blockData instanceof WallSign) {
            direction = ((Directional) blockData).getFacing().getOppositeFace();
        } else if (blockData instanceof Sign) {
            direction = getMajorDirection(((Rotatable) blockData).getRotation().getOppositeFace());
        } else {
            throw new IllegalArgumentException("Cannot get direction of " + blockData.getClass().getSimpleName());
        }
        return sign.getBlock().getRelative(direction);
    }

    /**
     * Convert a blockface to a major direction
     *
     * @param face The face to get the major direction from
     * @return The major direction. For middle directions it will return the next clockwise direction
     */
    public static BlockFace getMajorDirection(BlockFace face) {
        switch (face) {
            case NORTH_WEST:
            case NORTH_NORTH_WEST:
            case NORTH_NORTH_EAST:
                return BlockFace.NORTH;
            case NORTH_EAST:
            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
                return BlockFace.EAST;
            case SOUTH_EAST:
            case SOUTH_SOUTH_EAST:
            case SOUTH_SOUTH_WEST:
                return BlockFace.SOUTH;
            case SOUTH_WEST:
            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
                return BlockFace.WEST;
            default:
                return face;
        }
    }

    /**
     * Opens the holder's inventory GUI
     *
     * @param holder Inventory holder
     * @param player Player on whose screen the GUI is going to be shown
     * @return Was the opening successful?
     */
    public static boolean openBlockGUI(InventoryHolder holder, Player player) {
        Inventory inventory = holder.getInventory();
        player.openInventory(inventory);

        return true;
    }
}
