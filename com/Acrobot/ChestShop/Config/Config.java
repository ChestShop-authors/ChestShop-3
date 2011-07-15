package com.Acrobot.ChestShop.Config;

import com.Acrobot.ChestShop.Logging.Logging;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Acrobot
 */
public class Config {
    private static File configFile = new File("plugins/ChestShop", "config.yml");
    private static File langFile = new File("plugins/ChestShop", "local.yml");

    private static Configuration config = new Configuration(configFile);
    private static Configuration language = new Configuration(langFile);


    public static void setUp() {
        config.load();
        for (Property def : Property.values()) {
            if (config.getProperty(def.name()) == null) {
                writeToFile(def.name() + ": " + def.getValue() + "\n#" + def.getComment(), configFile);
            }
        }
        config.load();

        language.load();
        for (Language def : Language.values()) {
            if (language.getProperty(def.name()) == null) {
                writeToFile(def.name() + ": \"" + def.toString() + '\"', langFile);
            }
        }
        language.load();
    }

    public static void writeToFile(String string, File file) {
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write('\n' + string);
            fw.close();
        } catch (Exception e) {
            Logging.log("Couldn't write to file - " + file.getName());
        }
    }

    public static boolean getBoolean(Property value) {
        return (Boolean) getValue(value.name());
    }

    public static String getString(Property value) {
        return (String) getValue(value.name());
    }

    public static int getInteger(Property value) {
        return Integer.parseInt(getValue(value.name()).toString());
    }

    public static double getDouble(Property value) {
        return config.getDouble(value.name(), -1);
    }

    public static String getColored(String msg) {
        return msg.replaceAll("&([0-9a-f])", "\u00A7$1");
    }

    public static String getLocal(Language lang) {
        return getColored(language.getString(Language.prefix.name()) + language.getString(lang.name()));
    }

    private static Object getValue(String node) {
        return config.getProperty(node);
    }

    public static String getPreferred() {
        config.load();
        return getString(Property.PREFERRED_ECONOMY_PLUGIN);
    }
}
