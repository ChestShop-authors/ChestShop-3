package com.Acrobot.Breeze.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

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
}
