package com.Acrobot.ChestShop;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public enum Permission {
    SHOP_CREATION("ChestShop.shop.create"),
    BUY("ChestShop.shop.buy"),
    BUY_ID("ChestShop.shop.buy."),
    SELL_ID("ChestShop.shop.sell."),
    SELL("ChestShop.shop.sell"),
    ADMIN("ChestShop.admin"),
    MOD("ChestShop.mod"),
    OTHER_NAME("ChestShop.name."),
    GROUP("ChestShop.group."),
    NOFEE("ChestShop.nofee");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public static PermissionHandler permissions;

    public static boolean has(Player player, Permission permission) {
        return has(player, permission.permission);
    }

    public static boolean has(Player player, String node) {
        if (permissions != null) return permissions.has(player, node) || permissions.has(player, node.toLowerCase());
        return player.hasPermission(node) || player.hasPermission(node.toLowerCase());
    }
    
    public static boolean otherName(Player p, String name){
        if (has(p, Permission.ADMIN)) return false;
        String node = OTHER_NAME + name;
        if (permissions != null) return permissions.has(p, node) || permissions.has(p, node.toLowerCase());
        return hasPermissionSet(p, node) || hasPermissionSet(p, node.toLowerCase());
    }
    
    private static boolean hasPermissionSet(Player p, String perm){
        return p.isPermissionSet(perm) && p.hasPermission(perm);
    }

    public String toString() {
        return permission;
    }
}
