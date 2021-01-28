package com.Acrobot.Breeze.Utils;

import com.Acrobot.Breeze.Utils.ImplementationFeatures.PaperLatestHolder;
import com.Acrobot.Breeze.Utils.ImplementationFeatures.PaperLatestState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.BiFunction;

public class ImplementationAdapter {

    private static BiFunction<Inventory, Boolean, InventoryHolder> HOLDER_PROVIDER;
    private static BiFunction<Block, Boolean, BlockState> STATE_PROVIDER;

    static {
        try {
            InventoryHolder.class.getMethod("getHolder", boolean.class);
            HOLDER_PROVIDER = PaperLatestHolder.PROVIDER;
        } catch (NoSuchMethodException e) {
            HOLDER_PROVIDER = (inventory, useSnapshot) -> inventory.getHolder();
        }
        try {
            Block.class.getMethod("getState", boolean.class);
            STATE_PROVIDER = PaperLatestState.PROVIDER;
        } catch (NoSuchMethodException e) {
            STATE_PROVIDER = (block, useSnapshot) -> block.getState();
        }
    }

    /**
     * Get the inventory's holder.
     * @param inventory     The inventory
     * @param useSnapshot   Whether or not the holder should be a snapshot (if possible)
     * @return The inventory's holder
     */
    public static InventoryHolder getHolder(Inventory inventory, boolean useSnapshot) {
        return HOLDER_PROVIDER.apply(inventory, useSnapshot);
    }

    /**
     * Get a block state
     * @param block         The block to get the state from
     * @param useSnapshot   Whether or not the state should be a snapshot (if possible)
     * @return The block's state
     */
    public static BlockState getState(Block block, boolean useSnapshot) {
        return STATE_PROVIDER.apply(block, useSnapshot);
    }
}
