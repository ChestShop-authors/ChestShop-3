package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Utils.SearchForBlock;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Default implements Protection {
    public boolean isProtected(Block block) {
        Sign sign = SearchForBlock.findSign(block);
        return (block != null) && ((SignUtil.isSign(block) && SignUtil.isValid((Sign) block.getState())) || sign != null);
    }

    public boolean canAccess(Player player, Block block) {
        Sign sign = SearchForBlock.findSign(block);
        return (block != null) && (SignUtil.isSign(block) && SignUtil.isValid((Sign) block.getState()) && ((Sign) block.getState()).getLine(0).equals(player.getName())) || (sign != null && sign.getLine(0).equals(player.getName()));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
