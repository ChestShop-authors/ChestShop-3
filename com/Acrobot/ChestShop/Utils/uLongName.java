package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.ConfigObject;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Acrobot
 */
public class uLongName {
    public static YamlConfiguration config;
    public static File configFile;

    public static String getName(final String shortName) {
        return config.getString(shortName, shortName);
    }

    public static void saveName(String name) {
        if (name.length() != 16) return;
        config.set(name.substring(0, 15), name);
        ConfigObject.reloadConfig(config, configFile);
    }

    public static String stripName(String name) {
        return (name.length() > 15 ? name.substring(0, 15) : name);
    }
}
