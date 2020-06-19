package com.Acrobot.Breeze.Utils;

/**
 * @author Acrobot
 */
public class NumberUtil {
    /**
     * Checks if the string is a integer
     *
     * @param string string to check
     * @return Is the string integer?
     */
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the string is a float
     *
     * @param string string to check
     * @return Is the string float?
     */
    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the string is a double
     *
     * @param string string to check
     * @return Is the string double?
     */
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the string is a short
     *
     * @param string string to check
     * @return Is the string short?
     */
    public static boolean isShort(String string) {
        try {
            Short.parseShort(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the string is a long
     *
     * @param string string to check
     * @return Is the string long?
     */
    public static boolean isLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the string is an enchantment
     *
     * @param string String to check
     * @return Is the string an enchantment?
     */
    public static boolean isEnchantment(String string) {
        try {
            Long.parseLong(string, 32);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Rounds the number up to two decimal points (Can be inaccurate due to using decimal-points)
     *
     * @param number Number to round
     * @return Rounded number
     */
    public static double roundUp(double number) {
        return Math.ceil(number * 100) / 100;
    }

    /**
     * Rounds the number down to two decimal points
     *
     * @param number Number to round
     * @return Rounded number
     */
    public static double roundDown(double number) {
        return Math.floor(number * 100) / 100;
    }

    /**
     * Converts the number (in seconds) to timer-like format, like 2:00 (minutes:seconds)
     *
     * @param number Number of seconds
     * @return Formatted string
     */
    public static String toTime(int number) {
        return String.format("%02d:%02d", (number % 3600) / 60, number % 60);
    }

    /**
     * Converts a number to roman (only 1-9, because of the enchantment decorations)
     *
     * @param number number to convert
     * @return Converted number
     */
    public static String toRoman(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            default:
                return Integer.toString(number);
        }
    }

    /**
     * Convert a long to an integer while not overflowing but returning Integer.MAX_VALUE
     *
     * @param number The long to convert
     * @return The integer value or Integer.MAX_VALUE on overflow
     */
    public static int toInt(long number) {
        return number > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) number;
    }
}
