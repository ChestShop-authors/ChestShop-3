package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
import com.daemitus.deadbolt.Deadbolt;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class DeadboltPlugin implements Protection {
    public static Deadbolt deadbolt;

    public boolean isProtected(Block block) {
        return Deadbolt.isProtected(block);
    }

    public boolean canAccess(Player player, Block block) {
        int length = (player.getName().length() > 15 ? 15 : player.getName().length());
        return Deadbolt.getAllNames(block).contains(player.getName().substring(0, length));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
