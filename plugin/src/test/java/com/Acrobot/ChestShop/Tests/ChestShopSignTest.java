package com.Acrobot.ChestShop.Tests;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChestShopSignTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1",
            "17",
            "64",
            "3000"
    })
    public void testLegalSignQuantityLine(String line) {
        assertTrue(validateSignLine(QUANTITY_LINE, line));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "-1",
            "0123",
            "four",
            "abc",
            "1 string",
            "string 3",
            "test 2 test",
            "1e10",
            "930271839"
    })
    public void testIllegalSignQuantityLine(String line) {
        assertFalse(validateSignLine(QUANTITY_LINE, line));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "B 1",
            "B FREE",
            "B 1K",
            "B 10M",
            "1K",
            "S 1K",
            "S 10M",
            "1K S"
    })
    public void testLegalSignPriceLine(String line) {
        assertTrue(validateSignLine(PRICE_LINE, line));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "B 1 : S",
            "B : S 2",
            "B S 2",
            "B 1 2",
            "10KB",
            "1 B:S -1M",
            "S 1MK",
            "B -1K : S10K",
            "B1Z"
    })
    public void testIllegalSignPriceLine(String line) {
        assertFalse(validateSignLine(PRICE_LINE, line));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Stone",
            "Diamond_Sword",
            "Diamond-Sword",
            "Diamond Sword",
            "Diamond Sword:203",
            "Diamond#123",
            "Diamond#az3",
            "Diamond#BE2",
            "Stone Sword:123#123",
            "Diamond#223:123",
            "?"
    })
    public void testLegalSignItemLine(String line) {
        assertTrue(validateSignLine(ITEM_LINE, line));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Stone Sword:as",
            "Dirt:23:432",
            "\u00A9",
    })
    public void testIllegalSignItemLine(String line) {
        assertFalse(validateSignLine(ITEM_LINE, line));
    }

    private boolean validateSignLine(int lineNumber, String line) {
        for (Pattern pattern : ChestShopSign.SHOP_SIGN_PATTERN[lineNumber - 1]) {
            if (pattern.matcher(line).matches()) {
                return true;
            }
        }
        return false;
    }
}
