package com.Acrobot.ChestShop.Protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Security {
    public static Protection protection = new Default();

    public static boolean protect(String name, Block block){
        return protection.protect(name, block);
    }

    public static boolean canAccess(Player player, Block block){
        return protection.canAccess(player, block);
    }

    public static boolean isProtected(Block block){
        return protection.isProtected(block);
    }
}
