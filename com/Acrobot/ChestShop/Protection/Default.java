package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
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
        if ((uSign.isSign(block) && uSign.isValid((Sign) block.getState())) || uBlock.findSign(block) != null) return true;
        if (!(block.getState() instanceof Chest)) return false;

        Chest neighbor = uBlock.findNeighbor(block);
        return neighbor != null && uBlock.findSign(neighbor.getBlock()) != null;
    }

    public boolean canAccess(Player player, Block block) {
        Sign sign = uBlock.findSign(block);
        Chest neighborChest = uBlock.findNeighbor(block);
        Sign neighborSign = (neighborChest != null ? uBlock.findSign(neighborChest.getBlock()) : null);

        String playerName = player.getName();
        String signLine = "";
        if (uSign.isSign(block) && uSign.isValid((Sign) block.getState())) signLine = ((Sign) block.getState()).getLine(0);
        if (sign != null) signLine = sign.getLine(0);
        if (neighborSign != null) signLine = neighborSign.getLine(0);

        return uLongName.stripName(playerName).equals(signLine);
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
