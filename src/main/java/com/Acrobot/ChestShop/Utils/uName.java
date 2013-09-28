package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Permission;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * @author Acrobot
 */
public class uName {
    public static YamlConfiguration config;
    public static File file;

    public static String getName(String shortName) {
        return config.getString(shortName, shortName);
    }

    public static void saveName(String name) {
        if (name.length() <= 15) {
            return;
        }

        config.set(stripName(name), name);
        reload();
    }

    public static String stripName(String name) {
        if (name.length() > 15) {
            return name.substring(0, 15);
        }

        return name;
    }

    public static String stripName(Player player) {
        return stripName(player.getName());
    }

    public static boolean canUseName(Player player, String name) {
        return stripName(player).equals(name) || Permission.otherName(player, name);
    }

    public static void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void reload() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }
}
