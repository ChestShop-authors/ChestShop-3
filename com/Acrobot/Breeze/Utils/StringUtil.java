package com.Acrobot.Breeze.Utils;

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
        string = string.toLowerCase();

        String[] split = string.split(Character.toString(separator));
        StringBuilder total = new StringBuilder(string.length());

        for (String s : split) {
            total.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(' ');
        }

        return total.toString().trim();
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
        StringBuilder b = new StringBuilder(array.length * 15);

        for (String str : array) {
            b.append(str).append(' ');
        }

        return b.toString();
    }
}
