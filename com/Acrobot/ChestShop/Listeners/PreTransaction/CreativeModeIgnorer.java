package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.CREATIVE_MODE_PROTECTION;

/**
 * @author Acrobot
 */
public class CreativeModeIgnorer implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreTransaction(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (Properties.IGNORE_CREATIVE_MODE && event.getClient().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(CREATIVE_MODE_PROTECTION);
        }
    }
}
