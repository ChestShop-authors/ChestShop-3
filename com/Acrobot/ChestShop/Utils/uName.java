package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.BreezeConfiguration;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class uName {
    public static BreezeConfiguration config;

    public static String getName(String shortName) {
        return config.getString(shortName, shortName);
    }

    public static void saveName(String name) {
        if (name.length() <= 15) {
            return;
        }

        config.set(stripName(name), name);
        config.reload();
    }

    public static String stripName(String name) {
        return (name.length() > 15 ? name.substring(0, 15) : name);
    }

    public static String shortenName(Player player) {
        return stripName(player.getName());
    }

    public static boolean canUseName(Player player, String name) {
        return shortenName(player).equals(name) || Permission.otherName(player, name);
    }
}
