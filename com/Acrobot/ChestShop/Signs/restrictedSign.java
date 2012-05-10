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
        return !uSign.isSign(blockUp) || hasPermission(player, ((Sign) blockUp.getState()).getLines());

    }

    public static boolean canDestroy(Player p, Sign sign) {
        Sign shopSign = getAssociatedSign(sign);
        return uSign.canAccess(p, shopSign);
    }

    public static Sign getAssociatedSign(Sign restricted) {
        Block down = restricted.getBlock().getRelative(BlockFace.DOWN);
        return uSign.isSign(down) ? (Sign) down.getState() : null;
    }

    public static boolean hasPermission(Player p, String[] lines) {
        if (Permission.has(p, Permission.ADMIN)) {
            return true;
        }

        for (String line : lines) {
            if (p.hasPermission(Permission.GROUP.toString() + line)) {
                return true;
            }
        }
        return false;
    }
}
