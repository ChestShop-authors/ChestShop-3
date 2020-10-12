package com.Acrobot.Breeze.Utils;

import java.util.regex.Pattern;

/**
 * @author bricefrisco
 */
public class QuantityUtil {
    private static final Pattern QUANTITY_LINE_WITH_COUNTER_PATTERN = Pattern.compile("^Q [1-9][0-9]{0,4} : C [0-9]{0,5}$");

    public static int parseQuantity(String quantityLine) throws IllegalArgumentException {
        if (quantityLineContainsCounter(quantityLine)) {
            return Integer.parseInt(quantityLine.split(" : ")[0].replace("Q ", ""));
        }

        return Integer.parseInt(quantityLine);
    }

    public static boolean quantityLineContainsCounter(String quantityLine) {
        return QUANTITY_LINE_WITH_COUNTER_PATTERN.matcher(quantityLine).matches();
    }
}
