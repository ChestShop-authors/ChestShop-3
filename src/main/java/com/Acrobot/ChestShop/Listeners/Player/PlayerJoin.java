// PlayerJoin.java
package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.Commands.Toggle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean ignoring;

        try {
            if (!event.getPlayer().hasPlayedBefore()) {
                ignoring = false; // Disable ignoring state for first-time players
            } else {
                ignoring = Toggle.isIgnoring(event.getPlayer());
            }

            Toggle.setIgnoring(event.getPlayer(), ignoring);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}