package com.Acrobot.ChestShop.Config;

/**
 * @author Acrobot
 */
public enum Property {
    PREFERRED_ECONOMY_PLUGIN("", "Preferred economy plugin (iConomy, BOSEconomy, Essentials). If you do not want to specify this, leave it blank."),
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
    MASK_CHESTS_AS_OTHER_BLOCKS(false, "Do you want to mask shop chests as other blocks? HIGHLY EXPERIMENTAL, CAN LAG!"),
    SHOW_MESSAGE_OUT_OF_STOCK(true, "Do you want to show \"Out of stock\" messages?"),
    SHOW_TRANSACTION_INFORMATION_CLIENT(true, "Do you want to show \"You bought/sold... \" messages?"),
    SHOW_TRANSACTION_INFORMATION_OWNER(true, "Do you want to show \"Somebody bought/sold... \" messages?");


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
