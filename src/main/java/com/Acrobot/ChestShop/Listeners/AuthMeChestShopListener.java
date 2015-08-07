package com.Acrobot.ChestShop.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome;

import fr.xephi.authme.api.NewAPI;

public class AuthMeChestShopListener implements Listener {

    NewAPI AuthMeAPI = NewAPI.getInstance();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPreTransaction(PreTransactionEvent event) {
        if (event.getClient() == null) {
            return;
        }

        Player player = event.getClient();

        if (AuthMeAPI.isUnrestricted(player)) {
            return;
        }

        if (AuthMeAPI.isAuthenticated(player)) {
            return;
        }
        
        event.setCancelled(TransactionOutcome.CLIENT_DOES_NOT_HAVE_PERMISSION);
    }
}
