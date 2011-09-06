package com.Acrobot.ChestShop.Signs;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class restrictedSign {
    public static boolean isRestrictedShop(Sign sign) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return uSign.isSign(blockUp) && isRestricted(((Sign) blockUp.getState()).getLines());
    }

    public static boolean isRestricted(String[] lines) {
        return lines[0].equalsIgnoreCase("[restricted]");
    }

    public static boolean isRestricted(Sign sign) {
        return sign.getLine(0).equalsIgnoreCase("[restricted]");
    }

    public static boolean canAccess(Sign sign, Player player) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        if (Permission.permissions == null || !uSign.isSign(blockUp) || Permission.has(player, Permission.ADMIN)) return true;

        String world = blockUp.getWorld().getName();
        String playerName = player.getName();

        sign = (Sign) blockUp.getState();

        for (int i = 1; i <= 3; i++) {
            if (Permission.permissions != null && Permission.permissions.inGroup(world, playerName, sign.getLine(i))) return true;
            if (player.hasPermission("ChestShop.group." + sign.getLine(i))) return true;
        }
        return false;
    }
}
