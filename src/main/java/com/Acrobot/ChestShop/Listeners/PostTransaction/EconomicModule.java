package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;

/**
 * @author Acrobot
 */
public class EconomicModule implements Listener {

    @EventHandler(ignoreCancelled = true)
    public static void onBuyTransaction(TransactionEvent event) {
        CurrencyTransferEvent currencyTransferEvent = new CurrencyTransferEvent(
                event.getExactPrice(),
                event.getClient(),
                event.getOwnerAccount().getUuid(),
                event.getTransactionType() == BUY ? CurrencyTransferEvent.Direction.PARTNER : CurrencyTransferEvent.Direction.INITIATOR
        );
        ChestShop.callEvent(currencyTransferEvent);
        if (!currencyTransferEvent.wasHandled()) {
            event.setCancelled(true);
        }
    }
}
