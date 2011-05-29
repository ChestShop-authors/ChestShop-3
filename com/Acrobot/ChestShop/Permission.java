package com.Acrobot.ChestShop;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public enum Permission {
    SHOP_CREATION("iConomyChestShop.shop.create"),
    EXCLUDE_ITEM("iConomyChestShop.shop.exclude"),
    ADMIN("iConomyChestShop.admin");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
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
}
