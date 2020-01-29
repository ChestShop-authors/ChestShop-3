package com.Acrobot.Breeze.Utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Locale;

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
        if (string == null || string.isEmpty()) {
            return string;
        }
        char[] separators = new char[]{separator};

        return WordUtils.capitalize(string.toLowerCase(Locale.ROOT), separators).replace(String.valueOf(separator), " ");
    }

    /**
     * Capitalizes every first letter of a word
     *
     * @param string String to reformat
     * @return Reformatted string
     * @see StringUtil#capitalizeFirstLetter(String, char)
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

    private static String characters = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~¦ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×áíóúñÑªº¿®¬½¼¡«»";
    private static int[] extraWidth = {4,2,5,6,6,6,6,3,5,5,5,6,2,6,2,6,6,6,6,6,6,6,6,6,6,6,2,2,5,6,5,6,7,6,6,6,6,6,6,6,6,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,4,6,6,3,6,6,6,6,6,5,6,6,2,6,5,3,6,6,6,6,6,6,6,4,6,6,6,6,6,6,5,2,5,7,6,6,6,6,6,6,6,6,6,6,6,6,4,6,3,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,6,3,6,6,6,6,6,6,6,7,6,6,6,2,6,6,8,9,9,6,6,6,8,8,6,8,8,8,8,8,6,6,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,6,9,9,9,5,9,9,8,7,7,8,7,8,8,8,7,8,8,7,9,9,6,7,7,7,7,7,9,6,7,8,7,6,6,9,7,6,7,1};

    /**
     * Get the width that a character is displayed with in the default resource pack.
     * This relies on a hardcoded character to width mapping and might not be precise in places.
     * @param c The character to get the width of
     * @return The width of the character (will return 10 for characters that we don't know the width of)
     */
    public static int getMinecraftCharWidth(char c) {
        if (c != ChatColor.COLOR_CHAR) {
            int index = characters.indexOf(c);
            if (index > -1) {
                return extraWidth[index];
            } else {
                return 10;
            }
        }
        return 0;
    }

    /**
     * Get the width that a string is displayed with in the default resource pack.
     * This relies on a hardcoded character to width mapping and might not be precise in places.
     * @param string The string to get the width of
     * @return The width of the string
     */
    public static int getMinecraftStringWidth(String string) {
        int width = 0;
        for (char c : string.toCharArray()) {
            width += getMinecraftCharWidth(c);
        }
        return width;
    }
}
