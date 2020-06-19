package com.Acrobot.ChestShop.Listeners.Player;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.UUIDs.PlayerDTO;

/**
 * @author Acrobot
 */
public class PlayerConnect implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onPlayerConnect(final PlayerJoinEvent event) {
        if (NameManager.getUuidVersion() < 0) {
            NameManager.setUuidVersion(event.getPlayer().getUniqueId().version());
        }

        final PlayerDTO playerDTO = new PlayerDTO(event.getPlayer());

        Bukkit.getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), () -> {
            if (NameManager.getAccount(playerDTO.getUniqueId()) != null) {
                NameManager.storeUsername(playerDTO);
            }
        });
    }
}
