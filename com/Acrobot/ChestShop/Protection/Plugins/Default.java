package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Default implements Protection {
    public boolean isProtected(Block block) {
        if (!(block.getState() instanceof Chest)) return false;
        if (uBlock.findSign(block) != null) return true;

        Chest neighbor = uBlock.findNeighbor(block);
        return neighbor != null && uBlock.findSign(neighbor.getBlock()) != null;
    }

    public boolean canAccess(Player player, Block block) {
        String playerName = player.getName();

        Sign sign = uBlock.findSign(block);
        if (sign != null) return uLongName.stripName(playerName).equals(sign.getLine(0));

        Chest neighborChest = uBlock.findNeighbor(block);
        Sign neighborSign = (neighborChest != null ? uBlock.findSign(neighborChest.getBlock()) : null);

        return neighborSign != null && uLongName.stripName(playerName).equals(neighborSign.getLine(0));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
