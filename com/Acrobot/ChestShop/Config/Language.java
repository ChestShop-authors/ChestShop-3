package com.Acrobot.ChestShop.Config;

/**
 * @author Acrobot
 */
public enum Language {
    prefix("&a[Shop] &f"),
    iteminfo("&aItem Information:&f"),

    ACCESS_DENIED("You don't have permission to do that!"),

    NOT_ENOUGH_MONEY("You don't have enough money!"),
    NOT_ENOUGH_MONEY_SHOP("Shop owner doesn't have enough money!"),

    NO_BUYING_HERE("You can't buy here!"),
    NO_SELLING_HERE("You can't sell here!"),

    NOT_ENOUGH_SPACE_IN_INVENTORY("You haven't got enough space in inventory!"),
    NOT_ENOUGH_SPACE_IN_CHEST("There isn't enough space in chest!"),
    NOT_ENOUGH_ITEMS_TO_SELL("You don't have enough items to sell!"),

    NOT_ENOUGH_STOCK("This shop is out of stock."),
    NOT_ENOUGH_STOCK_IN_YOUR_SHOP("Your %material shop is out of stock!"),

    YOU_BOUGHT_FROM_SHOP("You bought %amount %item from %owner for %price."),
    SOMEBODY_BOUGHT_FROM_YOUR_SHOP("%buyer bought %amount %item for %price from you."),

    YOU_SOLD_TO_SHOP("You sold %amount %item to %buyer for %price."),
    SOMEBODY_SOLD_TO_YOUR_SHOP("%seller sold %amount %item for %price to you."),

    YOU_CANNOT_CREATE_SHOP("You can't create this type of shop!"),
    NO_CHEST_DETECTED("Couldn't find a chest!"),
    ANOTHER_SHOP_DETECTED("Another player's shop detected!"),
    CANNOT_ACCESS_THE_CHEST("You don't have permissions to access this chest!"),

    PROTECTED_SHOP("Successfully protected the shop with LWC!"),
    SHOP_CREATED("Shop successfully created!"),

    NO_PERMISSION("You don't have permissions to do that!"),
    INCORRECT_ITEM_ID("You have specified invalid item id!");


    private final String text;

    private Language(String def) {
        text = def;
    }

    public String toString() {
        return text;
    }
}
