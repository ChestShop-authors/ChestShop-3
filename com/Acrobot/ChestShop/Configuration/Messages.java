package com.Acrobot.ChestShop.Configuration;

import org.bukkit.ChatColor;

/**
 * @author Acrobot
 */
public class Messages {
    public static String prefix = ChatColor.GREEN + "[Shop] " + ChatColor.RESET;
    public static String iteminfo = ChatColor.GREEN + "Item Information: " + ChatColor.RESET;

    public static byte NEWLINE_iteminfo; ///////////////////////////////////////////////////

    public static String ACCESS_DENIED = "You don't have permission to do that!";

    public static byte NEWLINE_ACCESS_DENIED; ///////////////////////////////////////////////////

    public static String NOT_ENOUGH_MONEY = "You don't have enough money!";
    public static String NOT_ENOUGH_MONEY_SHOP = "Shop owner doesn't have enough money!";
    
    public static String DEPOSIT_FAILED = "Money deposit to shop owner failed!";
    public static String DEPOSIT_FAILED_OWNER = "Money deposit to your account failed, shop transaction aborted!";

    public static byte NEWLINE_NOT_ENOUGH_MONEY_SHO; ///////////////////////////////////////////////////

    public static String NO_BUYING_HERE = "You can't buy here!";
    public static String NO_SELLING_HERE = "You can't sell here!";

    public static byte NEWLINE_NO_SELLING_HERE; ///////////////////////////////////////////////////

    public static String NOT_ENOUGH_SPACE_IN_INVENTORY = "You haven't got enough space in inventory!";
    public static String NOT_ENOUGH_SPACE_IN_CHEST = "There isn't enough space in chest!";
    public static String NOT_ENOUGH_ITEMS_TO_SELL = "You don't have enough items to sell!";

    public static byte NEWLINE_NOT_ENOUGH_ITEMS_TO_SELL; ///////////////////////////////////////////////////

    public static String NOT_ENOUGH_STOCK = "This shop is out of stock.";
    public static String NOT_ENOUGH_STOCK_IN_YOUR_SHOP = "Your %material shop is out of stock!";

    public static byte NEWLINE_ENOUGH_STOCK_IN_YOUR_SHOP; ///////////////////////////////////////////////////

    public static String YOU_BOUGHT_FROM_SHOP = "You bought %item from %owner for %price.";
    public static String SOMEBODY_BOUGHT_FROM_YOUR_SHOP = "%buyer bought %item for %price from you.";

    public static byte NEWLINE_SOMEBODY_BOUGHT_FROM_YOUR_SHOP; ///////////////////////////////////////////////////

    public static String YOU_SOLD_TO_SHOP = "You sold %item to %buyer for %price.";
    public static String SOMEBODY_SOLD_TO_YOUR_SHOP = "%seller sold %item for %price to you.";

    public static byte NEWLINE_SOMEBODY_SOLD_TO_YOUR_SHOP; ///////////////////////////////////////////////////

    public static String YOU_CANNOT_CREATE_SHOP = "You can't create this type of shop!";
    public static String NO_CHEST_DETECTED = "Couldn't find a chest!";
    public static String INVALID_SHOP_DETECTED = "The shop cannot be used! (It might lack a chest!)";
    public static String CANNOT_ACCESS_THE_CHEST = "You don't have permissions to access this chest!";

    public static byte NEWLINE_CANNOT_ACCESS_THE_CHEST; ///////////////////////////////////////////////////

    public static String PROTECTED_SHOP = "Successfully protected the shop with LWC!";
    public static String SHOP_CREATED = "Shop successfully created!";
    public static String SHOP_REFUNDED = "You have been refunded %amount.";

    public static byte NEWLINE_SHOP_REFUNDED; ///////////////////////////////////////////////////

    public static String RESTRICTED_SIGN_CREATED = "Sign succesfully created!";

    public static byte NEWLINE_RESTRICTED_SIGN_CREATED; ///////////////////////////////////////////////////

    public static String NO_PERMISSION = "You don't have permissions to do that!";
    public static String INCORRECT_ITEM_ID = "You have specified invalid item id!";
    public static String NOT_ENOUGH_PROTECTIONS = "Could not create a protection!";

    public static byte NEWLINE_NOT_ENOUGH_PROTECTIONS; ///////////////////////////////////////////////////

    public static String CANNOT_CREATE_SHOP_HERE = "You can't create shop here!";

    public static String prefix(String message) {
        return prefix + message;
    }
}
