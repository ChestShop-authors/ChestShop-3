package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class MaskChest implements Runnable {
    private final BlockFace[] bf = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    public void run() {
        Player[] players = ChestShop.getBukkitServer().getOnlinePlayers();

        for (Player player : players) {
            World world = player.getWorld();
            Location location = player.getLocation();

            int pX = location.getBlockX();
            int pY = location.getBlockY();
            int pZ = location.getBlockZ();

            int radius = 25;

            for (int x = -radius; x < radius; x++) {
                for (int y = -radius; y < radius; y++) {
                    for (int z = -radius; z < radius; z++) {
                        Block block = world.getBlockAt(x + pX, y + pY, z + pZ);

                        if (block.getType() == Material.CHEST) {
                            if (uBlock.findSign(block) != null) {
                                Chest neighbor = uBlock.findNeighbor(block);
                                Material nMat = returnNearestMat(block);
                                if (neighbor != null) {
                                    player.sendBlockChange(neighbor.getBlock().getLocation(), nMat, (byte) 0);
                                }
                                player.sendBlockChange(block.getLocation(), nMat, (byte) 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private Material returnNearestMat(Block block) {
        for (BlockFace face : bf) {
            Block faceBlock = block.getRelative(face);
            Material type = faceBlock.getType();
            if (type != Material.AIR && !uSign.isSign(faceBlock) && type != Material.CHEST) return type;
        }
        return Material.CHEST;
    }
}
