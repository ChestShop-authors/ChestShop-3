package com.Acrobot.ChestShop.Listeners.Shop.PreTransaction;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Config.Property.IGNORE_CREATIVE_MODE;
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

        if (Config.getBoolean(IGNORE_CREATIVE_MODE) && event.getClient().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(CREATIVE_MODE_PROTECTION);
        }
    }
}
