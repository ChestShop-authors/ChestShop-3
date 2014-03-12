package com.Acrobot.ChestShop.Tests;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.Acrobot.ChestShop.Listeners.PreShopCreation.PriceChecker.onPreShopCreation;
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
    public void testPrice() {
        PreShopCreationEvent event = new PreShopCreationEvent(null, null, getPriceString("B 1"));
        onPreShopCreation(event);
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("S 1"));
        onPreShopCreation(event);
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("B 1:S 1"));
        onPreShopCreation(event);
        assertFalse(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("BS 1"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());

        event = new PreShopCreationEvent(null, null, getPriceString("B 1S0"));
        onPreShopCreation(event);
        assertTrue(event.isCancelled());
    }
}
