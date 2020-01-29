package com.Acrobot.Breeze.Utils;

import java.math.BigDecimal;
import java.util.Locale;

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
                BigDecimal price = new BigDecimal(part);

                if (price.compareTo(MAX) > 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
                    return NO_PRICE;
                } else {
                    return price;
                }
            } catch (NumberFormatException ignored) {}
        }

        return NO_PRICE;
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
        if (NumberUtil.isDouble(text)) {
            return true;
        }

        return text.trim().equalsIgnoreCase(FREE_TEXT);
    }
}
