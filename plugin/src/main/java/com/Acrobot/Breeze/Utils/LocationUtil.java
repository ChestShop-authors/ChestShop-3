package com.Acrobot.Breeze.Utils;

import org.bukkit.Location;

/**
 * An utility class providing various methods to deal with locations
 *
 * @author Acrobot
 */
public class LocationUtil {
    /**
     * Returns a string representing the location
     *
     * @param location Location represented
     * @return Representation of the location
     */
    public static String locationToString(Location location) {
        return '[' + location.getWorld().getName() + "] " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }
}
