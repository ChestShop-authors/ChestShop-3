package com.Acrobot.ChestShop.Utils;

import org.bukkit.util.config.Configuration;

/**
 * @author Acrobot
 */
public class uLongName {
    public static Configuration config;

    public static String getName(final String shortName){
        return config.getString(shortName, shortName);
    }

    public static void saveName(String name){
        if (!(name.length() > 15)) return;
        String shortName = name.substring(0, 15);
        config.setProperty(shortName, name);
        reloadConfig();
    }

    public static String stripName(String name) {
        return (name.length() > 15 ? name.substring(0, 15) : name);
    }

    private static void reloadConfig(){
        config.save();
        config.load();
    }
}
