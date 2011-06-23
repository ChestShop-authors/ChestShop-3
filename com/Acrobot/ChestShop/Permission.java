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
    ADMIN("ChestShop.admin");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public static PermissionHandler permissions;

    public static boolean has(Player player, Permission permission) {
        String node = permission.permission;
        return has(player, node);
    }

    public static boolean has(Player player, String node) {
        if (permissions != null) {
            return permissions.has(player, node);
        } else {
            return !node.contains("exclude") && (!node.contains("admin") || player.isOp());
        }
    }

    public String toString() {
        return permission;
    }
}
