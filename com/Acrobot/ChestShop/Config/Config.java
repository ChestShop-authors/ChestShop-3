package com.Acrobot.ChestShop.Config;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Utils.uName;
import com.nijikokun.register.payment.forChestShop.Methods;

import java.io.File;

/**
 * @author Acrobot
 */
public class Config {
    public static BreezeConfiguration normalConfig;
    public static BreezeConfiguration languageConfig;

    public static void setup() {
        File configFolder = ChestShop.getFolder();

        normalConfig = BreezeConfiguration.loadConfiguration(new File(configFolder, "config.yml"), Property.getValues());
        languageConfig = BreezeConfiguration.loadConfiguration(new File(configFolder, "local.yml"), Language.getValues());

        uName.config = BreezeConfiguration.loadConfiguration(new File(configFolder, "longName.storage"));

        Methods.setPreferred(Config.getString(Property.PREFERRED_ECONOMY_PLUGIN));
    }

    public static boolean getBoolean(Property value) {
        return (Boolean) getValue(value.name());
    }

    public static float getFloat(Property value) {
        return getFloat(value.name());
    }

    public static float getFloat(String value) {
        return new Float(getValue(value).toString());
    }

    public static String getString(Property value) {
        return (String) getValue(value.name());
    }

    public static int getInteger(Property value) {
        return Integer.parseInt(getValue(value.name()).toString());
    }

    public static double getDouble(Property value) {
        return getDouble(value.name());
    }

    public static double getDouble(String value) {
        return new Double(getValue(value).toString());
    }

    private static String getColored(String msg) {
        return msg.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    public static String getLocal(Language lang) {
        return getColored(languageConfig.getString(Language.prefix.name()) + languageConfig.getString(lang.name()));
    }

    public static boolean exists(String value) {
        return getValue(value) != null;
    }

    private static Object getValue(String node) {
        return normalConfig.get(node);
    }
}
