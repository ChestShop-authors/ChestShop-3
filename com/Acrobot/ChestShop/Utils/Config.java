package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Logging.Logging;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

/**
 * @author Acrobot
 */
public class Config {
    private static File configFile = new File("plugins/ChestShop/config.yml");
    private static File langFile = new File("plugins/ChestShop/local.yml");

    private static Configuration config = new Configuration(configFile);
    private static Configuration language = new Configuration(langFile);

    public static HashMap<String, Object> defaultValues = new HashMap<String, Object>();
    private static String langChar = Character.toString((char) 167);


    public static void setUp() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Logging.log("Successfully created blank configuration file");
            } catch (Exception e) {
                Logging.log("Couldn't create configuration file!");
            }
        }
        config.load();
        language.load();

        Defaults.set();
    }

    public static boolean getBoolean(String node) {
        return (Boolean) getValue(node);
    }

    public static String getString(String node) {
        return getColored((String) getValue(node));
    }

    public static int getInteger(String node) {
        return Integer.parseInt(getValue(node).toString());
    }

    public static double getDouble(String node) {
        return config.getDouble(node, -1);
    }

    private static Object getValue(String node, Configuration configuration, File file) {
        if (configuration.getProperty(node) == null) {
            try {
                Object defaultValue = defaultValues.get(node);
                if (defaultValue != null) {
                    FileWriter fw = new FileWriter(file, true);
                    fw.write('\n' + node + ": " + defaultValue);
                    fw.close();
                }
                configuration.load();
            } catch (Exception e) {
                Logging.log("Failed to update config file!");
            }
        }
        return configuration.getProperty(node);
    }

    public static String getColored(String msg) {
        return msg.replaceAll("&", langChar);
    }

    public static String getLocal(String node) {
        return getColored(getDefaultLocal("prefix") + (String) getDefaultLocal(node));
    }

    private static Object getValue(String node) {
        return getValue(node, config, configFile);
    }

    private static Object getDefaultLocal(String node) {
        return getValue(node, language, langFile);
    }
}
