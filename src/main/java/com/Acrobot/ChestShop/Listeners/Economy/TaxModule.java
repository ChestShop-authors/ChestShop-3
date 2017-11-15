package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Acrobot
 */
public class TaxModule implements Listener {

    private static BigDecimal getTax(BigDecimal price, float taxAmount) {
        return price.multiply(BigDecimal.valueOf(taxAmount).divide(BigDecimal.valueOf(100), BigDecimal.ROUND_DOWN));
    }

    private static boolean isServerAccount(UUID name) {
        return NameManager.isAdminShop(name);
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onCurrencyAdd(CurrencyAddEvent event) {
        if (event.isAdded()) {
            return;
        }

        UUID target = event.getTarget();

        if (NameManager.isServerEconomyAccount(target)) {
            return;
        }

        float taxAmount = isServerAccount(target) ? Properties.SERVER_TAX_AMOUNT : Properties.TAX_AMOUNT;

        if (taxAmount == 0) {
            return;
        }

        BigDecimal tax = getTax(event.getAmount(), taxAmount);

        if (NameManager.getServerEconomyAccount() != null) {
            CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(
                    tax,
                    NameManager.getServerEconomyAccount().getUuid(),
                    event.getWorld());
            ChestShop.callEvent(currencyAddEvent);
        }

        event.setAmount(event.getAmount().subtract(tax));
    }
}
