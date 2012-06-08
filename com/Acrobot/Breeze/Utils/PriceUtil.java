package com.Acrobot.Breeze.Utils;

/**
 * @author Acrobot
 */
public class PriceUtil {
    public static final double NO_PRICE = -1;
    public static final double FREE = 0;

    public static final String FREE_TEXT = "free";

    /**
     * Gets the price from the text
     * @param text Text to check
     * @param indicator Price indicator (for example, B for buy)
     * @return price
     */
    public static double get(String text, char indicator) {
        String[] split = text.replace(" ", "").toLowerCase().split(":");
        String character = String.valueOf(indicator);

        for (String part : split) {
            if (!part.contains(character)) {
                continue;
            }

            part = part.replace(character, "");

            if (part.equals(FREE_TEXT)) {
                return FREE;
            }

            if (NumberUtil.isDouble(part)) {
                return Double.valueOf(part);
            }
        }

        return NO_PRICE;
    }

    /**
     * Gets the buy price from te text
     * @param text Text to check
     * @return Buy price
     */
    public static double getBuyPrice(String text) {
        return get(text, 'b');
    }

    /**
     * Gets the sell price from te text
     * @param text Text to check
     * @return Sell price
     */
    public static double getSellPrice(String text) {
        return get(text, 's');
    }
}
