package com.Acrobot.ChestShop.Protection;

import com.Acrobot.ChestShop.Protection.Plugins.Default;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
import org.bukkit.block.Block;
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
        Sign sign = uBlock.findSign(uBlock.findChest(block).getBlock());
        if (sign == null) sign = uBlock.findSign(block);

        return (sign == null || sign.getLine(0).equals(uLongName.stripName(p.getName())));
    }
}
