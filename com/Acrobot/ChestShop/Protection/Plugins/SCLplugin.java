package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
import com.webkonsept.bukkit.simplechestlock.SCL;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class SCLplugin implements Protection {
    public static SCL scl;

    public boolean isProtected(Block block) {
        return scl.chests.isLocked(block);
    }

    public boolean canAccess(Player player, Block block) {
        return scl.chests.getOwner(block).equalsIgnoreCase(player.getName());
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
