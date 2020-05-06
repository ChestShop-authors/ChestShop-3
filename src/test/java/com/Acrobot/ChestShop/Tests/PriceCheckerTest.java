package com.Acrobot.ChestShop.Tests;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;

import static com.Acrobot.ChestShop.Listeners.PreShopCreation.PriceChecker.onPreShopCreation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Andrzej Pomirski (Acrobot)
 */
@RunWith(JUnit4.class)
public class PriceCheckerTest {

    String[] getPriceString(String prices) {
        return new String[]{null, null, prices, null};
    }

    @Test
    public void testLegalBuyPrice() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("B 1"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactBuyPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(1));

        event = new PreShopCreationEvent(null, null, getPriceString("B FREE"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactBuyPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), PriceUtil.FREE);

        assertFalse(event.isCancelled());
    }

    @Test
    public void testLegalSellPrice() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("S 1"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(1));

        event = new PreShopCreationEvent(null, null, getPriceString("S FREE"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), PriceUtil.FREE);

        assertFalse(event.isCancelled());
    }

    @Test
    public void testIllegalBuyPrice() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("10 B 1"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("1EB100000000000"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());
    }

    @Test
    public void testLegalBuyAndSellPrices() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("B 2:S 1"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(1));
        assertEquals(PriceUtil.getExactBuyPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(2));
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("2 B:S 1"));

        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(1));
        assertEquals(PriceUtil.getExactBuyPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(2));
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("2 B:1 S"));

        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(1));
        assertEquals(PriceUtil.getExactBuyPrice(event.getSignLine(ChestShopSign.PRICE_LINE)), BigDecimal.valueOf(2));
        assertFalse(event.isCancelled());
    }

    @Test
    public void testIllegalPrices() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("BS 1"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("B 1S0"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("B -100"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());

        String priceString = "5 B 5";
        assertEquals(PriceUtil.getExactBuyPrice(priceString), PriceUtil.NO_PRICE);

        priceString = "5 S 5";
        assertEquals(PriceUtil.getExactSellPrice(priceString), PriceUtil.NO_PRICE);

        priceString = "5 B 5:5 S 5";
        assertEquals(PriceUtil.getExactBuyPrice(priceString), PriceUtil.NO_PRICE);
        assertEquals(PriceUtil.getExactSellPrice(priceString), PriceUtil.NO_PRICE);
    }

    @Test
    public void testRemovingTrailingZeroes() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("S.7500000000"));
        onPreShopCreation(event);
        assertEquals(event.getSignLine(ChestShopSign.PRICE_LINE), "S.75");

        event = new PreShopCreationEvent(null, null, getPriceString("S7500000000"));
        onPreShopCreation(event);
        assertEquals(event.getSignLine(ChestShopSign.PRICE_LINE), "S7500000000");

        event = new PreShopCreationEvent(null, null, getPriceString("S.75000:B.75000"));
        onPreShopCreation(event);
        assertEquals(event.getSignLine(ChestShopSign.PRICE_LINE), "S.75:B.75");

        event = new PreShopCreationEvent(null, null, getPriceString("S75000:B.75000"));
        onPreShopCreation(event);
        assertEquals(event.getSignLine(ChestShopSign.PRICE_LINE), "S75000:B.75");
    }
}
