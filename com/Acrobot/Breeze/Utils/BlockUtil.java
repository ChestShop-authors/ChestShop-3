package com.Acrobot.Breeze.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Attachable;

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
        return block.getState() instanceof Sign;
    }

    /**
     * Checks if the block is a chest
     *
     * @param block Block to check
     * @return Is this block a chest?
     */
    public static boolean isChest(Block block) {
        return block.getType() == Material.CHEST;
    }

    /**
     * Gets the block to which the sign is attached
     *
     * @param sign Sign which is attached
     * @return Block to which the sign is attached
     */
    public static Block getAttachedFace(Sign sign) {
        return sign.getBlock().getRelative(((Attachable) sign.getData()).getAttachedFace());
    }

    /**
     * Opens the block's inventory's GUI
     *
     * @param block  Block
     * @param player Player on whose screen the GUI is going to be shown
     * @return Was the opening successful?
     */
    public static boolean openBlockGUI(Block block, Player player) {
        if (!(block instanceof InventoryHolder)) {
            return false;
        }

        Inventory inventory = ((InventoryHolder) block).getInventory();
        player.openInventory(inventory);

        return true;
    }
}
