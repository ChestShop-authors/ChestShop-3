package com.Acrobot.Breeze.Utils;

import com.google.common.base.Joiner;
import org.apache.commons.lang.WordUtils;

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
        char[] separators = new char[] {separator};

        return WordUtils.capitalizeFully(string, separators);
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
        return Joiner.on(' ').join(array);
    }
}
