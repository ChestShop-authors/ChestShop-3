package com.Acrobot.ChestShop.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

/**
 * @author Acrobot
 */
public class SearchForBlock {

    public static Chest findChest(Sign sign) {
        Block block = sign.getBlock();
        return findChest(block);
    }

    public static Chest findChest(Block block) {
        for (BlockFace bf : BlockFace.values()) {
            Block faceBlock = block.getFace(bf);
            if (faceBlock.getType() == Material.CHEST) {
                return (Chest) faceBlock.getState();
            }
        }
        return null;
    }

    public static Sign findSign(Block block) {
        for (BlockFace bf : BlockFace.values()) {
            Block faceBlock = block.getFace(bf);
            if (SignUtil.isSign(faceBlock)) {
                Sign sign = (Sign) faceBlock.getState();
                if (SignUtil.isValid(sign)) {
                    return sign;
                }
            }
        }
        return null;
    }
    
    public static Chest findNeighbor(Chest chest){
        BlockFace[] bf = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
        Block chestBlock = chest.getBlock();
        for (BlockFace blockFace : bf) {
            Block neighborBlock = chestBlock.getFace(blockFace);
            if (neighborBlock.getType() == Material.CHEST) {
                return (Chest) neighborBlock.getState();
            }
        }
        return null; //Shame, we didn't find double chest :/
    }
}
