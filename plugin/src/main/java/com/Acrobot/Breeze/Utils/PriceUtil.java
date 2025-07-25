package com.Acrobot.Breeze.Utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

/**
 * @author Acrobot
 */
public class PriceUtil {
    public static final BigDecimal NO_PRICE = BigDecimal.valueOf(-1);
    public static final BigDecimal FREE = BigDecimal.valueOf(0);
    public static final BigDecimal MAX = BigDecimal.valueOf(Double.MAX_VALUE);

    public static final String FREE_TEXT = "free";

    public static final char BUY_INDICATOR = 'b';
    public static final char SELL_INDICATOR = 's';

    private static final Map<Character, Integer> MULTIPLIERS = Map.of(
            'k', 1000,
            'm', 1000000
    );

    /**
     * Gets the exact price from the text
     *
     * @param text      Text to check
     * @param indicator Price indicator (for example, B for buy)
     * @return exact price
     */
    public static BigDecimal getExact(String text, char indicator) {
        String[] split = text.replace(" ", "").toLowerCase(Locale.ROOT).split(":");
        String character = String.valueOf(indicator).toLowerCase(Locale.ROOT);

        for (String part : split) {
            if (!part.startsWith(character) && !part.endsWith(character)) {
                continue;
            }

            part = part.replace(character, "");

            if (part.equals(FREE_TEXT)) {
                return FREE;
            }

            BigDecimal amountMultiplier = getMultiplier(part);
            part = stripMultiplierSuffix(part);


            try {
                BigDecimal price = new BigDecimal(part).multiply(amountMultiplier);

                if (price.compareTo(MAX) > 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                    return NO_PRICE;
                } else {
                    return price;
                }
            } catch (NumberFormatException ignored) {}
        }

        return NO_PRICE;
    }

    /**
     * Utility method to remove all defined suffix multipliers (as defined in {@link PriceUtil#MULTIPLIERS}
     * @param part String to modify
     * @return The string with all defined multiplier suffixes removed
     */
    public static String stripMultiplierSuffix(String part) {
        for (Character c : MULTIPLIERS.keySet()) {
            part = StringUtils.stripEnd(part, c.toString());
        }

        return part;
    }

    /**
     * Determines how much to multiply the amount based on the last character, as mapped in {@link PriceUtil#MULTIPLIERS}
     * @param part A string that can be parsed as BigDecimal, with an optional suffix (like 100M, 15.5K, 32)
     * @return BigDecimal
     */
    public static BigDecimal getMultiplier(String part) {
        char suffix = part.charAt(part.length()-1);

        return new BigDecimal(MULTIPLIERS.getOrDefault(suffix, 1));
    }

    /**
     * Tests if a string contains only a single multiplier character,
     * as defined in {@link PriceUtil#MULTIPLIERS}
     * @param part String to text
     * @return true if the given string has 0 or 1 multiplier characters
     */
    public static boolean hasSingleMultiplier(String part) {
        int count = 0;

        for (Character c : MULTIPLIERS.keySet()) {
            if (part.contains(c.toString())) count++;

            if (count > 1) return true;
        }

        return false;
    }

    /**
     * Gets the price from the text
     *
     * @param text      Text to check
     * @param indicator Price indicator (for example, B for buy)
     * @return price
     * @deprecated Use {@link #getExact(String, char)}
     */
    @Deprecated
    public static double get(String text, char indicator) {
        return getExact(text, indicator).doubleValue();
    }

    /**
     * Gets the exact buy price from the text
     *
     * @param text Text to check
     * @return Exact buy price
     */
    public static BigDecimal getExactBuyPrice(String text) {
        return getExact(text, BUY_INDICATOR);
    }

    /**
     * Gets the exact sell price from the text
     *
     * @param text Text to check
     * @return Exact sell price
     */
    public static BigDecimal getExactSellPrice(String text) {
        return getExact(text, SELL_INDICATOR);
    }

    /**
     * Gets the buy price from the text
     *
     * @param text Text to check
     * @return Buy price
     * @deprecated Use {@link #getExactBuyPrice(String)}
     */
    @Deprecated
    public static double getBuyPrice(String text) {
        return getExactBuyPrice(text).doubleValue();
    }

    /**
     * Gets the sell price from the text
     *
     * @param text Text to check
     * @return Sell price
     * @deprecated Use {@link #getExactSellPrice(String)}
     */
    @Deprecated
    public static double getSellPrice(String text) {
        return getExactSellPrice(text).doubleValue();
    }

    /**
     * Tells if there is a buy price
     *
     * @param text Price text
     * @return If there is a buy price
     */
    public static boolean hasBuyPrice(String text) {
        return hasPrice(text, BUY_INDICATOR);
    }

    /**
     * Tells if there is a sell price
     *
     * @param text Price text
     * @return If there is a sell price
     */
    public static boolean hasSellPrice(String text) {
        return hasPrice(text, SELL_INDICATOR);
    }

    /**
     * Tells if there is a price with the specified indicator
     *
     * @param text      Price text
     * @param indicator Price indicator
     * @return If the text contains indicated price
     */
    public static boolean hasPrice(String text, char indicator) {
        return getExact(text, indicator).compareTo(NO_PRICE) != 0;
    }

    /**
     * Checks if the string is a valid price
     *
     * @param text Text to check
     * @return Is the string a valid price
     */
    public static boolean isPrice(String text) {
        text = PriceUtil.stripMultiplierSuffix(text.toLowerCase(Locale.ROOT));

        if (NumberUtil.isDouble(text)) {
            return true;
        }

        return text.trim().equalsIgnoreCase(FREE_TEXT);
    }
}
