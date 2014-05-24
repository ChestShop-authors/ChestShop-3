package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Acrobot
 */
public class PlayerConnect implements Listener {
    @EventHandler
    public static void onPlayerConnect(PlayerJoinEvent event) {
        NameManager.storeUsername(event.getPlayer());
    }
}
