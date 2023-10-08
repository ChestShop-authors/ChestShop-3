package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
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
    private static final String TAX_RECEIVED_MESSAGE = "Applied a tax of %1$f percent (%2$.2f) to the received amount for a resulting price of %3$.2f";
    private static final String TAX_SENT_MESSAGE = "Reduced buy price by tax of %1$f percent (%2$.2f) for a resulting price of %3$.2f as the buyer has the buy tax bypass permission";

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
                BigDecimal taxedAmount = event.getAmountReceived().subtract(tax);
                event.setAmountReceived(taxedAmount);
                if (NameManager.getServerEconomyAccount() != null) {
                    ChestShop.callEvent(new CurrencyAddEvent(
                            tax,
                            NameManager.getServerEconomyAccount().getUuid(),
                            event.getWorld()));
                }
                ChestShop.getShopLogger().info(String.format(TAX_RECEIVED_MESSAGE, taxAmount, tax, taxedAmount));
            }
        } else if (event.getDirection() == CurrencyTransferEvent.Direction.PARTNER && Permission.has(event.getInitiator(), Permission.NO_BUY_TAX)) {
            // Reduce paid amount as the buyer has permission to not pay taxes
            BigDecimal taxSent = getTaxAmount(event.getAmountSent(), taxAmount);
            BigDecimal taxedSentAmount = event.getAmountSent().subtract(taxSent);
            event.setAmountSent(taxedSentAmount);
            ChestShop.getShopLogger().info(String.format(TAX_SENT_MESSAGE, taxAmount, taxSent, taxedSentAmount));

            // Reduce the amount that the seller receives anyways even though tax wasn't paid as that shouldn't make a difference for the seller
            BigDecimal taxReceived = getTaxAmount(event.getAmountReceived(), taxAmount);
            BigDecimal taxedReceivedAmount = event.getAmountReceived().subtract(taxReceived);
            event.setAmountReceived(taxedReceivedAmount);
            ChestShop.getShopLogger().info(String.format(TAX_RECEIVED_MESSAGE, taxAmount, taxReceived, taxedReceivedAmount));
        }
    }
}