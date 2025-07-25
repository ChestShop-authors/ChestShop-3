package com.Acrobot.Breeze.Utils;

import com.google.common.collect.ImmutableMap;

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

    private static final Map<Character, BigDecimal> MULTIPLIERS = ImmutableMap.of(
            'k', new BigDecimal(1000),
            'm', new BigDecimal(1000000)
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

            try {
                BigDecimal price = parseMultipliedPrice(part);

                if (isValidPrice(price)) {
                    return price;
                } else {
                    return NO_PRICE;
                }
            } catch (NumberFormatException ignored) {}
        }

        return NO_PRICE;
    }

    /**
     * Utility method to get the price multiplied by a single multiplier (as defined in {@link PriceUtil#MULTIPLIERS})
     * @param part String to parse
     * @return The parsed price multiplied by the multiplier, or the original number if no multiplier is found
     * @throws NumberFormatException If the string is not a valid number
     */
    private static BigDecimal parseMultipliedPrice(String part) throws NumberFormatException {
        for (Map.Entry<Character, BigDecimal> entry : MULTIPLIERS.entrySet()) {
            String suffix = entry.getKey().toString();
            if (part.endsWith(suffix)) {
                String priceString = part.substring(0, part.length() - suffix.length());
                BigDecimal number = new BigDecimal(priceString);
                BigDecimal multiplier = entry.getValue();
                return number.multiply(multiplier);
            }
        }

        return new BigDecimal(part);
    }

    /**
     * Tests if a string contains only a single multiplier character,
     * as defined in {@link PriceUtil#MULTIPLIERS}
     * @param part String to text
     * @return true if the given string has 0 or 1 multiplier characters
     */
    public static boolean hasSingleMultiplier(String part) {
        boolean foundMultiplier = false;

        for (Character c : MULTIPLIERS.keySet()) {
            if (foundMultiplier) {
                return false;
            }

            foundMultiplier = part.contains(c.toString());
        }

        return true;
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
        text = text.trim().toLowerCase(Locale.ROOT);

        if (text.equals(FREE_TEXT)) {
            return true;
        }

        try {
            return isValidPrice(parseMultipliedPrice(text));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the price is valid.
     * @param price Price to check
     * @return True if the price is valid (between 0 and max, inclusive), false otherwise
     */
    private static boolean isValidPrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) >= 0 && price.compareTo(MAX) <= 0;
    }
}
