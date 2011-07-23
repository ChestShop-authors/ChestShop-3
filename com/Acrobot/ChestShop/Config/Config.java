package com.Acrobot.ChestShop.Config;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Logging.Logging;
import com.Acrobot.ChestShop.Utils.uLongName;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Acrobot
 */
public class Config {
    private static File configFile;
    private static File langFile;
    private static Configuration config = new Configuration(new File(ChestShop.folder, "config.yml"));
    private static Configuration language;

    public static void setUp() {
        setUpConfigurations();

        reloadConfig();
        config.load();

        reloadLanguage();
        language.load();

        uLongName.config = new Configuration(new File(ChestShop.folder, "longName.storage"));
        uLongName.config.load();
    }

    private static void reloadConfig(){
        config.load();
        for (Property def : Property.values()) {
            if (config.getProperty(def.name()) == null) {
                writeToFile(def.name() + ": " + def.getValue() + "\n#" + def.getComment(), configFile);
            }
        }
    }

    private static void reloadLanguage(){
        language.load();
        for (Language def : Language.values()) {
            if (language.getProperty(def.name()) == null) {
                writeToFile(def.name() + ": \"" + def.toString() + '\"', langFile);
            }
        }
    }

    private static void setUpConfigurations(){
        configFile = new File(ChestShop.folder, "config.yml");
        langFile = new File(ChestShop.folder, "local.yml");

        config = new Configuration(configFile);
        language = new Configuration(langFile);
    }

    private static void writeToFile(String string, File file) {
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

    private static String getColored(String msg) {
        return msg.replaceAll("&([0-9a-f])", "\u00A7$1");
    }

    public static String getLocal(Language lang) {
        return getColored(language.getString(Language.prefix.name()) + language.getString(lang.name()));
    }

    private static Object getValue(String node) {
        return config.getProperty(node);
    }

    public static String getPreferred() {
        config = new Configuration(new File("plugins/ChestShop", "config.yml"));
        config.load();
        
        return getString(Property.PREFERRED_ECONOMY_PLUGIN);
    }
}
