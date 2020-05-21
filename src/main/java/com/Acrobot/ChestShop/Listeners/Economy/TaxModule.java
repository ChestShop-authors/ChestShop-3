package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Permission;
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

    private static float getTax(UUID partner) {
        float taxAmount = NameManager.isAdminShop(partner) || NameManager.isServerEconomyAccount(partner)
                ? Properties.SERVER_TAX_AMOUNT : Properties.TAX_AMOUNT;

        if (taxAmount == 0) {
            return 0;
        }

        return taxAmount;
    }

    private static BigDecimal getTaxAmount(BigDecimal price, float taxAmount) {
        return price.multiply(BigDecimal.valueOf(taxAmount)).divide(BigDecimal.valueOf(100), Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onCurrencyTransfer(CurrencyTransferEvent event) {
        if (event.wasHandled()) {
            return;
        }

        float taxAmount = getTax(event.getPartner());
        if (taxAmount == 0) {
            return;
        }

        if (!Permission.has(event.getInitiator(), event.getDirection() == CurrencyTransferEvent.Direction.PARTNER ? Permission.NO_BUY_TAX : Permission.NO_SELL_TAX)) {
            if (!NameManager.isServerEconomyAccount(event.getReceiver())) {
                BigDecimal tax = getTaxAmount(event.getAmountReceived(), taxAmount);
                event.setAmountReceived(event.getAmountReceived().subtract(tax));
                if (NameManager.getServerEconomyAccount() != null) {
                    ChestShop.callEvent(new CurrencyAddEvent(
                            tax,
                            NameManager.getServerEconomyAccount().getUuid(),
                            event.getWorld()));
                }
            }
        } else if (event.getDirection() == CurrencyTransferEvent.Direction.PARTNER && Permission.has(event.getInitiator(), Permission.NO_BUY_TAX)) {
            event.setAmountSent(event.getAmountSent().subtract(getTaxAmount(event.getAmountSent(), taxAmount)));
            event.setAmountReceived(event.getAmountReceived().subtract(getTaxAmount(event.getAmountReceived(), taxAmount)));
        }
    }
}
