package com.Acrobot.ChestShop.Listeners.PostTransaction;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

import java.math.BigDecimal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;

/**
 * @author Acrobot
 */
public class EconomicModule implements Listener {
    @EventHandler
    public static void onBuyTransaction(TransactionEvent event) {
        if (event.getTransactionType() != BUY) {
            return;
        }
        
        CurrencyTransferEvent currencyTransferEvent = new CurrencyTransferEvent(BigDecimal.valueOf(event.getPrice()), 
        														event.getClient().getUniqueId(), 
        														event.getOwner().getUniqueId(), 
        														event.getSign().getWorld());

        ChestShop.callEvent(currencyTransferEvent);
       
    }

    @EventHandler
    public static void onSellTransaction(TransactionEvent event) {
        if (event.getTransactionType() != SELL) {
            return;
        }
        
        CurrencyTransferEvent currencyTransferEvent = new CurrencyTransferEvent(BigDecimal.valueOf(event.getPrice()), 
        														event.getOwner().getUniqueId(),
        														event.getClient().getUniqueId(), 
																event.getSign().getWorld());

        ChestShop.callEvent(currencyTransferEvent);
    }
}
