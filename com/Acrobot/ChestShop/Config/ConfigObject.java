package com.Acrobot.ChestShop.Config;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Utils.uLongName;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Acrobot
 */
public class ConfigObject {
    private final File configFile = new File(ChestShop.folder, "config.yml");
    private final File langFile = new File(ChestShop.folder, "local.yml");
    private final Configuration config = new Configuration(configFile);
    private final Configuration language = new Configuration(langFile);

    public ConfigObject() {
        if (!ChestShop.folder.exists()) ChestShop.folder.mkdir();

        reloadConfig();
        config.load();

        reloadLanguage();
        language.load();

        uLongName.config = new Configuration(new File(ChestShop.folder, "longName.storage"));
        uLongName.config.load();
    }

    private void reloadConfig() {
        config.load();
        for (Property def : Property.values()) {
            if (config.getProperty(def.name()) == null) {
                writeToFile('\n' + def.name() + ": " + def.getValue() + "\n#" + def.getComment(), configFile);
            }
        }
    }

    private void reloadLanguage() {
        language.load();
        for (Language def : Language.values()) {
            if (language.getProperty(def.name()) == null) {
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
            System.out.println("Couldn't write to file - " + file.getName());
        }
    }

    public Configuration getLanguageConfig() {
        return language;
    }

    public Object getProperty(String property) {
        return config.getProperty(property);
    }
}
