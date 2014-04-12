package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author Acrobot
 */
public class PlayerConnect implements Listener {
    @EventHandler
    public static void onPlayerJoin(PlayerLoginEvent event) {
        String name = event.getPlayer().getName();

        if (name != null && ChestShopSign.isAdminShop(name)) {
            if (Permission.has(event.getPlayer(), Permission.ADMIN)) {
                return;
            }

            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Oh no you don't. (ChestShop logged your IP to server log!)");

            ChestShop.getBukkitLogger().severe(event.getAddress() + " tried to log in on Admin Shop's account!");
        }
    }

    @EventHandler
    public static void onPlayerConnect(PlayerJoinEvent event) {
        NameManager.storeUsername(event.getPlayer());
    }
}
