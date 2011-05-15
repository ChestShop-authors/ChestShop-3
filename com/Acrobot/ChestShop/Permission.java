package com.Acrobot.ChestShop;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class Permission {
    public static PermissionHandler permissions;

    public static boolean has(Player player, String permission) {
        if (permissions != null) {
            return permissions.has(player, permission);
        } else {
            return !permission.contains("exclude") && (!permission.contains("admin") || player.isOp());
        }
    }
}
