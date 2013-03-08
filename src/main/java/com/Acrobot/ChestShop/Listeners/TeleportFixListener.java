package com.Acrobot.ChestShop.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author Acrobot
 */
public class TeleportFixListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getPlayer().closeInventory();
    }
}
