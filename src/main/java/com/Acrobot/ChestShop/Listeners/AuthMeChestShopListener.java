package com.Acrobot.ChestShop.Listeners;

import org.bukkit.event.Listener;

public class AuthMeChestShopListener implements Listener {

    /*NewAPI AuthMeAPI = NewAPI.getInstance();

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
    }*/
}
