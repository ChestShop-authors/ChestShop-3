package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
import com.daemitus.lockette.Lockette;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class LockettePlugin implements Protection {
    public static Lockette lockette;

    public boolean isProtected(Block block) {
        return Lockette.isProtected(block);
    }

    public boolean canAccess(Player player, Block block) {
        int length = (player.getName().length() > 15? 15 : player.getName().length());
        return player.getName().substring(0, length).equals(Lockette.getOwnerName(block));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
