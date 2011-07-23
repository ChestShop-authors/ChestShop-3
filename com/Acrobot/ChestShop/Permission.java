package com.Acrobot.ChestShop;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public enum Permission {
    SHOP_CREATION("ChestShop.shop.create"),
    EXCLUDE_ITEM("ChestShop.shop.exclude"),
    BUY("ChestShop.shop.buy"),
    SELL("ChestShop.shop.sell"),
    ADMIN("ChestShop.admin"),
    MOD("ChestShop.mod");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public static PermissionHandler permissions;

    public static boolean has(Player player, Permission permission) {
        return has(player, permission.permission);
    }

    public static boolean has(Player player, String node) {
        //return !node.contains("exclude") && !node.contains ("create.") && ((!node.contains("admin") && !node.contains("mod")) || player.isOp());
        if (permissions != null) return permissions.has(player, node);
        return player.hasPermission(node);
    }

    public String toString() {
        return permission;
    }
}
