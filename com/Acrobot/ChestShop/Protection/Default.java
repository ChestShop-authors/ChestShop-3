package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Utils.BlockSearch;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Default implements Protection {
    public boolean isProtected(Block block) {
        if ((SignUtil.isSign(block) && SignUtil.isValid((Sign) block.getState())) || BlockSearch.findSign(block) != null) {
            return true;
        } else {
            if (!(block.getState() instanceof Chest)) {
                return false;
            }
            if (BlockSearch.findSign(block) != null) {
                return true;
            }
            Chest neighbor = BlockSearch.findNeighbor(block);
            if (neighbor != null && BlockSearch.findSign(neighbor.getBlock()) != null) {
                return true;
            }
        }

        return false;
    }

    public boolean canAccess(Player player, Block block) {
        Sign sign = BlockSearch.findSign(block);
        Chest nChest = BlockSearch.findNeighbor(block);
        Sign nSign = (nChest != null ? BlockSearch.findSign(nChest.getBlock()) : null);
        return ((SignUtil.isSign(block) && SignUtil.isValid((Sign) block.getState()) && ((Sign) block.getState()).getLine(0).equals(player.getName())) || (sign != null && sign.getLine(0).equals(player.getName())))
                || (nSign != null && nSign.getLine(0).equals(player.getName()));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
