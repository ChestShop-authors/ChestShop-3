package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Permission;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class uName {
    public static YamlConfiguration config;
    public static File file;

    public static final String BANK_PREFIX = "$";
    public static final String BANK_PREFIX_QUOTED = Pattern.quote(BANK_PREFIX);
    public static final String BANK_PREFIX_REPLACE = "^" + BANK_PREFIX_QUOTED;

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

    public static String shortenName(Player player) {
        return stripName(player.getName());
    }

    public static boolean canUseName(Player player, String name) {
        return shortenName(player).equals(name) || Permission.otherName(player, name);
    }

    public static String stripBankPrefix(String name) {
        return name.replaceFirst(BANK_PREFIX_REPLACE, "");
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
