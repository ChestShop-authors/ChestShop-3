package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Acrobot
 */
public class PlayerConnect implements Listener {
    @EventHandler
    public static void onPlayerConnect(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override
            public void run() {
                NameManager.storeUsername(event.getPlayer());
            }
        });
    }
}
