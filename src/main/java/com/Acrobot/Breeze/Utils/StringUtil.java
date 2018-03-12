package com.Acrobot.Breeze.Utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.Collection;

/**
 * @author Acrobot
 */
public class StringUtil {

    /**
     * Capitalizes every first letter of a word
     *
     * @param string    String to reformat
     * @param separator Word separator
     * @return Reformatted string
     */
    public static String capitalizeFirstLetter(String string, char separator) {
        char[] separators = new char[]{separator};

        return WordUtils.capitalizeFully(string, separators).replace(String.valueOf(separator), " ");
    }

    /**
     * Capitalizes every first letter of a word
     *
     * @param string String to reformat
     * @return Reformatted string
     * @see com.Acrobot.Breeze.Utils.StringUtil#capitalizeFirstLetter(String, char)
     */
    public static String capitalizeFirstLetter(String string) {
        return capitalizeFirstLetter(string, ' ');
    }

    /**
     * Joins a String array
     *
     * @param array array to join
     * @return Joined array
     */
    public static String joinArray(String[] array) {
        return String.join(" ", array);
    }

    /**
     * Joins an iterable
     *
     * @param array Iterable
     * @return Joined iterable
     */
    public static String joinArray(Collection<String> array) {
        return String.join(" ", array);
    }

    /**
     * Strips colour codes from a string
     * @param string String to strip
     * @return Stripped string
     */
    public static String stripColourCodes(String string) {
        return ChatColor.stripColor(string);
    }

    /**
     * Stips colour codes from an array of strings
     * @param strings Strings to strip the codes from
     * @return Stripped strings
     */
    public static String[] stripColourCodes(String[] strings) {
        String[] output = new String[strings.length];

        for (int i = 0; i < strings.length; i++) {
            output[i] = stripColourCodes(strings[i]);
        }

        return output;
    }
}
