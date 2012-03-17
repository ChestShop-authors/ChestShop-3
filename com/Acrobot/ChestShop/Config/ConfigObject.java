package com.Acrobot.ChestShop.Config;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Utils.uLongName;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Acrobot
 */
public class ConfigObject {
    private final File configFile = new File(ChestShop.folder, "config.yml");
    private final File langFile = new File(ChestShop.folder, "local.yml");
    private final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    private final YamlConfiguration language = YamlConfiguration.loadConfiguration(langFile);

    public ConfigObject() {
        if (!ChestShop.folder.exists()) ChestShop.folder.mkdir();

        reloadConfig();
        load(config, configFile);

        reloadLanguage();
        load(language, langFile);

        uLongName.configFile = new File(ChestShop.folder, "longName.storage");
        uLongName.config = YamlConfiguration.loadConfiguration(uLongName.configFile);
    }

    private void reloadConfig() {
        for (Property def : Property.values()) {
            if (config.get(def.name()) == null) {
                writeToFile('\n' + def.name() + ": " + def.getValue() + "\n#" + def.getComment(), configFile);
            }
        }
    }

    private void reloadLanguage() {
        for (Language def : Language.values()) {
            if (language.get(def.name()) == null) {
                writeToFile('\n' + def.name() + ": \"" + def.toString() + '\"', langFile);
            }
        }
    }

    private static void writeToFile(String string, File file) {
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(string);
            fw.close();
        } catch (Exception e) {
            System.err.println("Couldn't write to file - " + file.getName());
        }
    }

    public Configuration getLanguageConfig() {
        return language;
    }

    public Object getProperty(String property) {
        return config.get(property);
    }

    public static void load(FileConfiguration config, File file) {
        try {
            config.load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void save(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void reloadConfig(FileConfiguration config, File file) {
        save(config, file);
        load(config, file);
    }
}
