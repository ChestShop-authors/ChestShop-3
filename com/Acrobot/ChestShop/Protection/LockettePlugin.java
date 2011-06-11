package com.Acrobot.ChestShop.Protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class LockettePlugin implements Protection {
    public static Lockette lockette;

    public boolean isProtected(Block block) {
        return Lockette.isProtected(block);
    }

    public boolean canAccess(Player player, Block block) {
        return player.getName().equals(Lockette.getProtectedOwner(block));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
