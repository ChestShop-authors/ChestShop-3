package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;

import static com.Acrobot.Breeze.Utils.PriceUtil.NO_PRICE;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_DOES_NOT_BUY_THIS_ITEM;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_DOES_NOT_SELL_THIS_ITEM;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;

/**
 * @author Acrobot
 */
public class PriceValidator implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPriceCheck(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        TransactionEvent.TransactionType transactionType = event.getTransactionType();
        BigDecimal price = event.getExactPrice();

        if (price.equals(NO_PRICE)) {
            if (transactionType == BUY) {
                event.setCancelled(SHOP_DOES_NOT_SELL_THIS_ITEM);
            } else {
                event.setCancelled(SHOP_DOES_NOT_BUY_THIS_ITEM);
            }
        }
    }
}
