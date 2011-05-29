package com.Acrobot.ChestShop.Protection;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public interface Protection {
    public boolean isProtected(Block block);

    public boolean canAccess(Player player, Block block);

    public boolean protect(String name, Block block);
}
