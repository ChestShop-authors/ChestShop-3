package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_DOES_NOT_BUY_THIS_ITEM;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_DOES_NOT_SELL_THIS_ITEM;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;

/**
 * @author Acrobot
 */
public class PriceValidator implements Listener {
    @EventHandler
    public static void onPriceCheck(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        TransactionEvent.TransactionType transactionType = event.getTransactionType();
        double price = event.getPrice();

        if (price == PriceUtil.NO_PRICE) {
            if (transactionType == BUY) {
                event.setCancelled(SHOP_DOES_NOT_BUY_THIS_ITEM);
            } else {
                event.setCancelled(SHOP_DOES_NOT_SELL_THIS_ITEM);
            }
        }
    }
}
