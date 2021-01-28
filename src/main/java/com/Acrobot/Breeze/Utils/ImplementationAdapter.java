package com.Acrobot.Breeze.Utils;

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
            Inventory.class.getMethod("getHolder", boolean.class);
            Class c = Class.forName("com.Acrobot.Breeze.Utils.ImplementationFeatures.PaperLatestHolder");
            HOLDER_PROVIDER = (BiFunction<Inventory, Boolean, InventoryHolder>) c.getDeclaredField("PROVIDER").get(null);
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            HOLDER_PROVIDER = (inventory, useSnapshot) -> inventory.getHolder();
        }
        try {
            Block.class.getMethod("getState", boolean.class);
            Class c = Class.forName("com.Acrobot.Breeze.Utils.ImplementationFeatures.PaperLatestState");
            STATE_PROVIDER = (BiFunction<Block, Boolean, BlockState>) c.getDeclaredField("PROVIDER").get(null);
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
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
