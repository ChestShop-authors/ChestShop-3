package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
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
        String pName = player.getName();
        int length = (pName.length() > 15 ? 15 : pName.length());
        String owner = Lockette.getProtectedOwner(block);
        return owner == null || pName.substring(0, length).equals(owner);
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
