package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.ChestShop;
import org.bukkit.util.config.Configuration;

import java.io.File;

/**
 * @author Acrobot
 */
public class uLongName {
    public static Configuration config = new Configuration(new File(ChestShop.folder, "longName.storage"));

    public static String getName(final String shortName) {
        return config.getString(shortName, shortName);
    }

    public static void saveName(String name) {
        if (name.length() != 16) return;
        config.setProperty(name.substring(0, 15), name);
        reloadConfig();
    }

    public static String stripName(String name) {
        return (name.length() > 15 ? name.substring(0, 15) : name);
    }

    private static void reloadConfig() {
        config.save();
        config.load();
    }
}
