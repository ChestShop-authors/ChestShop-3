package com.Acrobot.ChestShop;

import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public enum Permission {
    SHOP_CREATION_BUY("ChestShop.shop.create.buy"),
    SHOP_CREATION_SELL("ChestShop.shop.create.sell"),

    SHOP_CREATION_ID("ChestShop.shop.create."),

    BUY("ChestShop.shop.buy"),
    BUY_ID("ChestShop.shop.buy."),

    SELL_ID("ChestShop.shop.sell."),
    SELL("ChestShop.shop.sell"),

    ADMIN("ChestShop.admin"),
    MOD("ChestShop.mod"),
    OTHER_NAME("ChestShop.name."),
    GROUP("ChestShop.group."),
    BANK("ChestShop.bank"),

    NOFEE("ChestShop.nofee"),
    DISCOUNT("ChestShop.discount.");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public static boolean has(Player player, Permission permission) {
        return has(player, permission.permission);
    }

    public static boolean has(Player player, String node) {
        return player.hasPermission(node) || player.hasPermission(node.toLowerCase());
    }

    public static boolean otherName(Player p, String name) {
        if (has(p, Permission.ADMIN)) {
            return false;
        }

        String node = OTHER_NAME + name;
        return hasPermissionSet(p, node) || hasPermissionSet(p, node.toLowerCase());
    }

    private static boolean hasPermissionSet(Player p, String perm) {
        return p.isPermissionSet(perm) && p.hasPermission(perm);
    }

    public String toString() {
        return permission;
    }
}
