package com.Acrobot.ChestShop.Configuration;

import com.Acrobot.Breeze.Configuration.Annotations.PrecededBySpace;
import com.Acrobot.Breeze.Configuration.Configuration;

/**
 * @author Acrobot
 */
public class Messages {
    public static String prefix = "&a[Shop] &r";
    public static String iteminfo = "&aItem Information: &r";

    @PrecededBySpace
    public static String ACCESS_DENIED = "You don't have permission to access that shop's storage container!";

    @PrecededBySpace
    public static String NOT_ENOUGH_MONEY = "You don't have enough money!";
    public static String NOT_ENOUGH_MONEY_SHOP = "Shop owner doesn't have enough money!";

    @PrecededBySpace
    public static String CLIENT_DEPOSIT_FAILED = "Money deposit to your account failed!";
    public static String SHOP_DEPOSIT_FAILED = "Money deposit to shop owner failed!";
    public static String NO_ECONOMY_ACCOUNT = "Economy account from shop owner doesn't exist!";

    @PrecededBySpace
    public static String NO_BUYING_HERE = "You can't buy here!";
    public static String NO_SELLING_HERE = "You can't sell here!";

    @PrecededBySpace
    public static String NOT_ENOUGH_SPACE_IN_INVENTORY = "You haven't got enough space in inventory!";
    public static String NOT_ENOUGH_SPACE_IN_CHEST = "There isn't enough space in chest!";
    public static String NOT_ENOUGH_ITEMS_TO_SELL = "You don't have enough items to sell!";
    public static String NOT_ENOUGH_SPACE_IN_YOUR_SHOP = "%price %item&7 shop at &r%world/%x/%y/%z&7 is full! (%seller tried to sell)";

    @PrecededBySpace
    public static String NOT_ENOUGH_STOCK = "This shop is out of stock.";
    public static String NOT_ENOUGH_STOCK_IN_YOUR_SHOP = "%price %item&7 shop at &r%world/%x/%y/%z&7 is out of stock! (%buyer tried to buy)";

    @PrecededBySpace
    public static String YOU_BOUGHT_FROM_SHOP = "You bought %item from %owner for %price.";
    public static String SOMEBODY_BOUGHT_FROM_YOUR_SHOP = "%buyer bought %item for %price from your shop at %world/%x/%y/%z.";

    @PrecededBySpace
    public static String YOU_SOLD_TO_SHOP = "You sold %item to %buyer for %price.";
    public static String SOMEBODY_SOLD_TO_YOUR_SHOP = "%seller sold %item for %price to your shop at %world/%x/%y/%z.";

    @PrecededBySpace
    public static String YOU_CANNOT_CREATE_SHOP = "You can't create this type of shop!";
    public static String NO_CHEST_DETECTED = "Couldn't find a chest!";
    public static String INVALID_SHOP_DETECTED = "The shop cannot be used!";
    public static String INVALID_SHOP_PRICE = "The shop has an invalid price!";
    public static String INVALID_SHOP_QUANTITY = "The shop has an invalid quantity!";
    public static String CANNOT_ACCESS_THE_CHEST = "You don't have permissions to access this chest!";

    @PrecededBySpace
    public static String SELL_PRICE_ABOVE_MAX = "Sell price is above maximum!";
    public static String SELL_PRICE_BELOW_MIN ="Sell price is below minimum!";
    public static String BUY_PRICE_ABOVE_MAX = "Buy price is above maximum!";
    public static String BUY_PRICE_BELOW_MIN ="Buy price is below minimum!";

    @PrecededBySpace
    public static String CLICK_TO_AUTOFILL_ITEM = "Click the sign with the item that this shop is for!";
    public static String NO_ITEM_IN_HAND = "You don't have an item in your hand to autofill!";

    @PrecededBySpace
    public static String PROTECTED_SHOP = "Successfully protected the shop with LWC!";
    public static String PROTECTED_SHOP_SIGN = "Successfully protected the shop sign with LWC!";
    public static String SHOP_CREATED = "Shop successfully created!";
    public static String SHOP_FEE_PAID = "You have been charged %amount";
    public static String SHOP_REFUNDED = "You have been refunded %amount.";
    public static String ITEM_GIVEN = "Given %item to %player.";

    @PrecededBySpace
    public static String RESTRICTED_SIGN_CREATED = "Sign successfully created!";

    @PrecededBySpace
    public static String PLAYER_NOT_FOUND = "Player not found!";
    public static String NO_PERMISSION = "You don't have permissions to do that!";
    public static String INCORRECT_ITEM_ID = "You have specified an invalid item id!";
    public static String NOT_ENOUGH_PROTECTIONS = "Could not create a protection!";

    @PrecededBySpace
    public static String CANNOT_CREATE_SHOP_HERE = "You can't create shop here!";

    @PrecededBySpace
    public static String TOGGLE_MESSAGES_OFF = "You will no longer receive messages from your shop(s).";
    public static String TOGGLE_MESSAGES_ON = "You will now receive messages from your shop(s).";

    @PrecededBySpace
    public static String TOGGLE_ACCESS_ON = "You can no longer trade at shops that you have access to";
    public static String TOGGLE_ACCESS_OFF = "You can now trade at shops that you have access to";

    public static String prefix(String message) {
        return Configuration.getColoured(prefix + message);
    }
}
