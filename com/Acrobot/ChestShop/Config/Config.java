package com.Acrobot.ChestShop.Config;

import com.LRFLEW.register.payment.forChestShop.Methods;

/**
 * @author Acrobot
 */
public class Config {
    private static ConfigObject config;

    public static void setup(ConfigObject cfg) {
        config = cfg;
        Methods.preferred = Config.getString(Property.PREFERRED_ECONOMY_PLUGIN);
    }

    public static boolean getBoolean(Property value) {
        return (Boolean) getValue(value.name());
    }

    public static float getFloat(Property value) {
        return new Float(getValue(value.name()).toString());
    }

    public static String getString(Property value) {
        return (String) getValue(value.name());
    }

    public static int getInteger(Property value) {
        return Integer.parseInt(getValue(value.name()).toString());
    }

    public static double getDouble(Property value) {
        return Double.parseDouble(getValue(value.name()).toString());
    }

    private static String getColored(String msg) {
        return msg.replaceAll("&([0-9a-f])", "\u00A7$1");
    }

    public static String getLocal(Language lang) {
        return getColored(config.getLanguageConfig().getString(Language.prefix.name()) + config.getLanguageConfig().getString(lang.name()));
    }

    private static Object getValue(String node) {
        return config.getProperty(node);
    }
}
