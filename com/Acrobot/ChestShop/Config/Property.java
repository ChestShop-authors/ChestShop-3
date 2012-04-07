package com.Acrobot.ChestShop.Config;

/**
 * @author Acrobot
 */
public enum Property {
    PREFERRED_ECONOMY_PLUGIN("", "WHEN NOT USING VAULT. Preferred economy plugin (iConomy, BOSEconomy, Essentials). If you do not want to specify this, leave it blank."),
    REVERSE_BUTTONS(false, "If true, people will buy with left-click and sell with right-click."),
    ALLOW_LEFT_CLICK_DESTROYING(true, "If true, if you left-click your own shop sign you won't open chest's inventory, but instead you will start destroying the sign."),
    STACK_UNSTACKABLES(false, "If true, ALL things (including food, etc.) will stack up to 64"),
    SERVER_ECONOMY_ACCOUNT("", "Economy account's name you want Admin Shops to be assigned to"),
    ADMIN_SHOP_NAME("Admin Shop", "First line of your admin shop should look like this"),
    SHOP_CREATION_PRICE(0, "Amount of money player must pay to create a shop"),
    LOG_TO_FILE(false, "If true, plugin will log transactions in its own file"),
    LOG_TO_CONSOLE(true, "Do you want ChestShop's messages to show up in console?"),
    LOG_TO_DATABASE(false, "If true, plugin will log transactions in EBean database"),
    GENERATE_STATISTICS_PAGE(false, "If true, plugin will generate shop statistics webpage."),
    STATISTICS_PAGE_PATH("plugins/ChestShop/website.html", "Where should your generated website be saved?"),
    RECORD_TIME_TO_LIVE(600, "How long should transaction information be stored?"),
    STATISTICS_PAGE_GENERATION_INTERVAL(60, "How often should the website be generated?"),
    USE_BUILT_IN_PROTECTION(true, "Do you want to use built-in protection against chest destruction?"),
    PROTECT_CHEST_WITH_LWC(false, "Do you want to protect shop chests with LWC?"),
    PROTECT_SIGN_WITH_LWC(false, "Do you want to protect shop signs with LWC?"),
    IGNORE_CREATIVE_MODE(true, "Do you want to allow using shops to people in creative mode?"),
    SHOW_MESSAGE_OUT_OF_STOCK(true, "Do you want to show \"Out of stock\" messages?"),
    SHOW_TRANSACTION_INFORMATION_CLIENT(true, "Do you want to show \"You bought/sold... \" messages?"),
    SHOW_TRANSACTION_INFORMATION_OWNER(true, "Do you want to show \"Somebody bought/sold... \" messages?"),
    TOWNY_INTEGRATION(false, "Do you want to only let people build inside shop plots?"),
    TOWNY_SHOPS_FOR_OWNERS_ONLY(true, "If true, only plot owners are able to build inside a shop plot. If false, every town's resident is able to build there."),

    WORLDGUARD_INTEGRATION(false, "Do you want to only let people build inside regions?"),
    WORLDGUARD_USE_FLAG(true, "Do you want to only let poeple build inside region flagged by doing /region regionName flag chestshop allow?"),

    TAX_AMOUNT(0, "Percent of the price that should go to the server's account. (100 = 100 percent)"),
    SERVER_TAX_AMOUNT(0, "Percent of the price that should go to the server's account when buying from an Admin Shop"),
    SHOP_REFUND_PRICE(0, "How much money do you get back when destroying a sign?"),
    ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK(false, "Do you want to allow other players to build a shop on a block where there's one already?"),
    SHOP_INTERACTION_INTERVAL(100, "(In 1/1000th of a second) How often can a player use a shop sign?"),
    HEROES_EXP(100, "How much Heroes exp should people get for creating a ChestShop?"),
    BLOCK_UPDATE(false, "EXPERIMENTAL: Should every ChestShop transaction result in a block update?"),
    ALLOW_PARTIAL_TRANSACTIONS(true, "Can shops be used even when the seller doesn't have enough items? (The price will be scaled adequatly to the item amount)");
    ALLOW_SIGN_CHEST_OPEN(true, "Can shop's chest be opened by owner with right-clicking a shop's sign?");


    private final Object value;
    private final String comment;

    private Property(Object value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public Object getValue() {
        return (value instanceof String ? "\"" + value + '\"' : value);
    }

    public String getComment() {
        return comment;
    }

    public String toString() {
        return name();
    }
}
