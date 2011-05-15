package com.Acrobot.ChestShop.Protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Default implements Protection{
    public boolean isProtected(Block block) {
        return false; //TODO: Make it check the sign's first line
    }

    public boolean canAccess(Player player, Block block) {
        return false; //TODO: Make it check the sign's first line
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
