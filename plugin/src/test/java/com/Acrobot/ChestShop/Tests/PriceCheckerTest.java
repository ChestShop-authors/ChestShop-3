package com.Acrobot.ChestShop.Tests;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static com.Acrobot.ChestShop.Listeners.PreShopCreation.PriceChecker.onPreShopCreation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Andrzej Pomirski (Acrobot)
 */
public class PriceCheckerTest {

    static String[] getPriceString(String prices) {
        return new String[]{null, null, prices, null};
    }

    @Test
    public void testLegalBuyPrice() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("B 1"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactBuyPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(1));

        event = new PreShopCreationEvent(null, null, getPriceString("B FREE"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.FREE, PriceUtil.getExactBuyPrice(ChestShopSign.getPrice(event.getSignLines())));

        assertFalse(event.isCancelled());
    }

    @Test
    public void testLegalBuyPriceWithMultipliers() {
        PreShopCreationEvent event = createEventFromString("B 1K");
        assertEquals(BigDecimal.valueOf(1000), getExactBuyPrice(event));

        event = createEventFromString("B 10M");
        assertEquals(BigDecimal.valueOf(10_000_000), getExactBuyPrice(event));

        event = createEventFromString("1K");
        assertEquals(BigDecimal.valueOf(1000), getExactBuyPrice(event));
    }

    @Test
    public void testLegalSellPriceWithMultipliers() {
        PreShopCreationEvent event = createEventFromString("S 1K");
        assertEquals(BigDecimal.valueOf(1000), getExactSellPrice(event));

        event = createEventFromString("S 10M");
        assertEquals(BigDecimal.valueOf(10_000_000), getExactSellPrice(event));

        event = createEventFromString("1K S");
        assertEquals(BigDecimal.valueOf(1000), getExactSellPrice(event));
    }

    @Test
    public void testLegalSellPrice() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("S 1"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(1));

        event = new PreShopCreationEvent(null, null, getPriceString("S FREE"));
        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(ChestShopSign.getPrice(event.getSignLines())), PriceUtil.FREE);

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
        assertEquals(PriceUtil.getExactSellPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(1));
        assertEquals(PriceUtil.getExactBuyPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(2));
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("2 B:S 1"));

        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(1));
        assertEquals(PriceUtil.getExactBuyPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(2));
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("2 B:1 S"));

        onPreShopCreation(event);
        assertEquals(PriceUtil.getExactSellPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(1));
        assertEquals(PriceUtil.getExactBuyPrice(ChestShopSign.getPrice(event.getSignLines())), BigDecimal.valueOf(2));
        assertFalse(event.isCancelled());
    }

    @Test
    public void testLegalBuyAndSellPricesWithMultipliers() {
        PreShopCreationEvent event = createEventFromString("B 2M:S 1K");
        assertEquals(BigDecimal.valueOf(1000), getExactSellPrice(event));
        assertEquals(BigDecimal.valueOf(2_000_000), getExactBuyPrice(event));
        assertFalse(event.isCancelled());

        event = createEventFromString("2K B:S 1M");
        assertEquals(BigDecimal.valueOf(1_000_000), getExactSellPrice(event));
        assertEquals(BigDecimal.valueOf(2000), getExactBuyPrice(event));
        assertFalse(event.isCancelled());

        event = createEventFromString("2K B:1 S");
        assertEquals(BigDecimal.valueOf(1), getExactSellPrice(event));
        assertEquals(BigDecimal.valueOf(2000), getExactBuyPrice(event));
        assertFalse(event.isCancelled());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 B:S -1M",
            "S 1MK",
            "B -1K : S10K",
            "B1Z"
    })
    public void testIllegalBuyAndSellPricesWithMultipliers(String line) {
        assertTrue(createEventFromString(line).isCancelled());
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
        assertEquals(PriceUtil.NO_PRICE, PriceUtil.getExactBuyPrice(priceString));

        priceString = "5 S 5";
        assertEquals(PriceUtil.NO_PRICE, PriceUtil.getExactSellPrice(priceString));

        priceString = "5 B 5:5 S 5";
        assertEquals(PriceUtil.NO_PRICE, PriceUtil.getExactBuyPrice(priceString));
        assertEquals(PriceUtil.NO_PRICE, PriceUtil.getExactSellPrice(priceString));
    }

    @Test
    public void testRemovingTrailingZeroes() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("S.7500000000"));
        onPreShopCreation(event);
        assertEquals("S.75", ChestShopSign.getPrice(event.getSignLines()));

        event = new PreShopCreationEvent(null, null, getPriceString("S7500000000"));
        onPreShopCreation(event);
        assertEquals("S7500000000", ChestShopSign.getPrice(event.getSignLines()));

        event = new PreShopCreationEvent(null, null, getPriceString("S.75000:B.75000"));
        onPreShopCreation(event);
        assertEquals("S.75:B.75", ChestShopSign.getPrice(event.getSignLines()));

        event = new PreShopCreationEvent(null, null, getPriceString("S75000:B.75000"));
        onPreShopCreation(event);
        assertEquals("S75000:B.75", ChestShopSign.getPrice(event.getSignLines()));
    }

    private static BigDecimal getExactSellPrice(PreShopCreationEvent event) {
        return PriceUtil.getExactSellPrice(ChestShopSign.getPrice(event.getSignLines()));
    }

    private static BigDecimal getExactBuyPrice(PreShopCreationEvent event) {
        return PriceUtil.getExactBuyPrice(ChestShopSign.getPrice(event.getSignLines()));
    }

    private static PreShopCreationEvent createEventFromString(String priceString) {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString(priceString));
        onPreShopCreation(event);

        return event;
    }

}
