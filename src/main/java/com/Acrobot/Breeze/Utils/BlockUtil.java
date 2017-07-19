package com.Acrobot.Breeze.Utils;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Attachable;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Map;

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
        return block.getType() == Material.SIGN_POST
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
    public static Block getAttachedBlock(Sign sign) {
        return sign.getBlock().getRelative(((Attachable) sign.getData()).getAttachedFace());
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

    private static final BoundingBox BLOCK = new BoundingBox(new Vector(), new Vector(1, 1, 1));
    private static final Map<Material, BoundingBox> BOXES = ImmutableMap.of(
            Material.WALL_SIGN, new BoundingBox(new Vector(0, 0.28125, 0), new Vector(1,  0.78125, 0.125)), // Default direction is South
            Material.SIGN_POST, new BoundingBox(new Vector(0.25, 0, 0.25), new Vector(0.75, 1, 0.75))
    );

    public static BoundingBox getBoundingBox(Block block) {
        BoundingBox box = BOXES.getOrDefault(block.getType(), BLOCK);

        MaterialData data = block.getState().getData();
        if (data instanceof Directional) {
            Directional directional = (Directional) data;
            box = box.getFacing(directional.getFacing());
        }

        return box;
    }

    public static class BoundingBox {
        private final Vector minimumPoint;
        private final Vector maximumPoint;

        BoundingBox(Vector minimumPoint, Vector maximumPoint) {
            while (minimumPoint.getX() < 0 || maximumPoint.getX() < 0) {
                minimumPoint.setX(1 + minimumPoint.getX());
                maximumPoint.setX(1 + maximumPoint.getX());
            }
            while (minimumPoint.getY() < 0 || maximumPoint.getY() < 0) {
                minimumPoint.setY(1 + minimumPoint.getY());
                maximumPoint.setY(1 + maximumPoint.getY());
            }
            while (minimumPoint.getZ() < 0 || maximumPoint.getZ() < 0) {
                minimumPoint.setZ(1 + minimumPoint.getZ());
                maximumPoint.setZ(1 + maximumPoint.getZ());
            }
            
            this.minimumPoint = min(minimumPoint, maximumPoint);
            this.maximumPoint = max(minimumPoint, maximumPoint);
        }

        private Vector min(Vector v1, Vector v2) {
            return new Vector(Math.min(v1.getX(), v2.getX()), Math.min(v1.getY(), v2.getY()), Math.min(v1.getZ(), v2.getZ()));
        }

        private Vector max(Vector v1, Vector v2) {
            return new Vector(Math.max(v1.getX(), v2.getX()), Math.max(v1.getY(), v2.getY()), Math.max(v1.getZ(), v2.getZ()));
        }

        /**
         * Checks if a certain vector hits the hitbox of a block.
         * Only really checks for Signs as we don't need the rest
         *
         * @param source    The start location including the direction
         * @param block     The block to check
         * @param distance  The maximum distance from the source location to check for
         * @param precision The precision of the steps of the trace
         * @return Does the vector intercept the box?
         */
        public boolean intercepts(Location source, Block block, double distance, double precision) {
            if (source.distanceSquared(block.getLocation()) > distance * distance) {
                return false;
            }
            Vector posMin = block.getLocation().toVector().add(minimumPoint);
            Vector posMax = block.getLocation().toVector().add(maximumPoint);
            Vector direction = source.getDirection();
            for (double d = 0; d < distance; d += precision) {
                Location check = source.clone().add(direction.clone().multiply(d));
                if (check.getX() >= posMin.getX() && check.getX() < posMax.getX()
                        && check.getY() >= posMin.getY() && check.getY() < posMax.getY()
                        && check.getZ() >= posMin.getZ() && check.getZ() < posMax.getZ()) {
                    return true;
                }
            }
            return false;
        }

        BoundingBox getFacing(BlockFace face) {
            Vector minPoint = minimumPoint.clone();
            Vector maxPoint = maximumPoint.clone();
            if (face == BlockFace.NORTH || face == BlockFace.EAST || face == BlockFace.SOUTH || face == BlockFace.WEST) {
                if (face.getModX() == 0) {
                    minPoint.setX(face.getModZ() * minimumPoint.getX());
                    maxPoint.setX(face.getModZ() * maximumPoint.getX());
                    minPoint.setZ(face.getModZ() * minimumPoint.getZ());
                    maxPoint.setZ(face.getModZ() * maximumPoint.getZ());
                } else if (face.getModZ() == 0) {
                    minPoint.setX(face.getModX() * minimumPoint.getZ());
                    maxPoint.setX(face.getModX() * maximumPoint.getZ());
                    minPoint.setZ(face.getModX() * minimumPoint.getX());
                    maxPoint.setZ(face.getModX() * maximumPoint.getX());
                }
            }
            return new BoundingBox(minPoint, maxPoint);
        }
    }
}
