package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.WeakHashMap;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SPAM_CLICKING_PROTECTION;

/**
 * @author Acrobot
 */
public class SpamClickProtector implements Listener {
    private final Map<Player, Long> TIME_OF_LATEST_CLICK = new WeakHashMap<Player, Long>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player clicker = event.getClient();

        if (TIME_OF_LATEST_CLICK.containsKey(clicker) && (System.currentTimeMillis() - TIME_OF_LATEST_CLICK.get(clicker)) < Properties.SHOP_INTERACTION_INTERVAL) {
            event.setCancelled(SPAM_CLICKING_PROTECTION);
            return;
        }

        TIME_OF_LATEST_CLICK.put(clicker, System.currentTimeMillis());
    }
}
