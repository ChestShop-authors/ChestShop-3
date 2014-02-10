package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;

/**
 * @author Acrobot
 */
public class TaxModule implements Listener {

    private static BigDecimal getTax(BigDecimal price, float taxAmount) {
        return price.multiply(BigDecimal.valueOf(taxAmount).divide(BigDecimal.valueOf(100)));
    }

    private static boolean isServerAccount(String name) {
        return ChestShopSign.isAdminShop(name);
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onCurrencyAdd(CurrencyAddEvent event) {
        if (event.isAdded()) {
            return;
        }

        String target = event.getTarget();

        if (target.equals(Economy.getServerAccountName())) {
            return;
        }

        float taxAmount = isServerAccount(target) ? Properties.SERVER_TAX_AMOUNT : Properties.TAX_AMOUNT;

        if (taxAmount == 0) {
            return;
        }

        BigDecimal tax = getTax(event.getAmount(), taxAmount);

        if (!Economy.getServerAccountName().isEmpty()) {
            CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(tax, Economy.getServerAccountName(), event.getWorld());
            ChestShop.callEvent(currencyAddEvent);
        }

        event.setAmount(event.getAmount().subtract(tax));
    }
}
