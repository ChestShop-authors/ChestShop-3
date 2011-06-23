package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Utils.BlockSearch;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Security {
    public static Protection protection = new Default();

    public static boolean protect(String name, Block block) {
        return protection.protect(name, block);
    }

    public static boolean canAccess(Player player, Block block) {
        return protection.canAccess(player, block);
    }

    public static boolean isProtected(Block block) {
        return protection.isProtected(block);
    }

    public static boolean canPlaceSign(Player p, Block block) {
        Chest chest = BlockSearch.findChest(block);
        Sign sign1 = BlockSearch.findSign(chest.getBlock());
        Sign sign2 = BlockSearch.findSign(block);

        return (sign1 == null || sign1.getLine(0).startsWith(p.getName())) && (sign2 == null || sign2.getLine(0).startsWith(p.getName()));
    }
}
