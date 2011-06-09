package com.Acrobot.ChestShop.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public enum DefaultValue {
    reverse_buttons(false, "If true, people will buy with left-click and sell with right-click."),
    serverEconomyAccount("", "Economy account's name you want Admin Shops to be assigned to"),
    logToFile(false, "If true, plugin will log transactions in its own file"),
    useDB(false, "If true, plugin will log transactions in EBean database"),
    adminShopName("Admin Shop", "First line of your admin shop should look like this"),
    generateStatisticsPage(false, "If true, plugin will generate shop statistics webpage."),
    DBtimeToLive(600, "How long should transaction information be stored?"),
    logToConsole(true, "Do you want ChestShop's messages to show up in console?");

    private Object value;
    private String comment;

    private static final Map<String, DefaultValue> names = new HashMap<String, DefaultValue>();

    private DefaultValue(Object value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public Object getValue() {
        return (value instanceof String ? "\"" + value + "\"" : value);
    }

    public String getComment() {
        return comment;
    }

    public String toString() {
        return name();
    }

    public static DefaultValue lookup(String name) {
        return names.get(name);
    }

    static {
        for (DefaultValue def : values()) {
            names.put(def.name(), def);
        }
    }
}
