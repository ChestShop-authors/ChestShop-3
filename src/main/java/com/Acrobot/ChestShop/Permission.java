package com.Acrobot.ChestShop;

import org.bukkit.command.CommandSender;
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

    NOFEE("ChestShop.nofee"),
    DISCOUNT("ChestShop.discount."),

    NOTIFY_TOGGLE("ChestShop.toggle");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public static boolean has(CommandSender sender, Permission permission) {
        return has(sender, permission.permission);
    }

    public static boolean has(CommandSender sender, String node) {
        return sender.hasPermission(node) || sender.hasPermission(node.toLowerCase());
    }

    public static boolean otherName(Player player, String name) {
        if (has(player, Permission.ADMIN)) {
            return false;
        }

        String node = OTHER_NAME + name;
        return hasPermissionSet(player, node) || hasPermissionSet(player, node.toLowerCase());
    }

    private static boolean hasPermissionSet(CommandSender sender, String permission) {
        return sender.isPermissionSet(permission) && sender.hasPermission(permission);
    }

    public String toString() {
        return permission;
    }
}
