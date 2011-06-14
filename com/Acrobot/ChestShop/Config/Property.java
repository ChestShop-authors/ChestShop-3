package com.Acrobot.ChestShop.Config;

/**
 * @author Acrobot
 */
public enum Property {
    REVERSE_BUTTONS (false, "If true, people will buy with left-click and sell with right-click."),
    SERVER_ECONOMY_ACCOUNT ("", "Economy account's name you want Admin Shops to be assigned to"),
    LOG_TO_FILE(false, "If true, plugin will log transactions in its own file"),
    LOG_TO_CONSOLE(true, "Do you want ChestShop's messages to show up in console?"),
    USE_DATABASE(false, "If true, plugin will log transactions in EBean database"),
    ADMIN_SHOP_NAME("Admin Shop", "First line of your admin shop should look like this"),
    GENERATE_STATISTICS_PAGE(false, "If true, plugin will generate shop statistics webpage."),
    STATISTICS_PAGE_PATH("plugins/ChestShop/website.html", "Where should your generated website be saved?"),
    RECORD_TIME_TO_LIVE(600, "How long should transaction information be stored?"),
    USE_BUILT_IN_PROTECTION(true, "Do you want to use built-in protection?"),
    PROTECT_CHEST_WITH_LWC(false, "Do you want to protect shop chests with LWC?"),
    PROTECT_SIGN_WITH_LWC(false, "Do you want to protect shop signs with LWC?");

    
    private Object value;
    private String comment;

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
