package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Default implements Protection {
    public boolean isProtected(Block block) {
        if ((uSign.isSign(block) && uSign.isValid((Sign) block.getState())) || uBlock.findSign(block) != null) {
            return true;
        } else {
            if (!(block.getState() instanceof Chest)) {
                return false;
            }
            if (uBlock.findSign(block) != null) {
                return true;
            }
            Chest neighbor = uBlock.findNeighbor(block);
            if (neighbor != null && uBlock.findSign(neighbor.getBlock()) != null) {
                return true;
            }
        }

        return false;
    }

    public boolean canAccess(Player player, Block block) {
        Sign sign = uBlock.findSign(block);
        Chest nChest = uBlock.findNeighbor(block);
        Sign nSign = (nChest != null ? uBlock.findSign(nChest.getBlock()) : null);
        return ((uSign.isSign(block) && uSign.isValid((Sign) block.getState()) && ((Sign) block.getState()).getLine(0).equals(player.getName())) || (sign != null && sign.getLine(0).equals(player.getName())))
                || (nSign != null && nSign.getLine(0).equals(player.getName()));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
