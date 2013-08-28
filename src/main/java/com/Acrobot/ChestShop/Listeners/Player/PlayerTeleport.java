package com.Acrobot.ChestShop.Listeners.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * A fix for a CraftBukkit bug.
 *
 * @author Acrobot
 */
public class PlayerTeleport implements Listener {

    @EventHandler
    public static void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer().getOpenInventory().getType() != InventoryType.CRAFTING) {
            event.getPlayer().closeInventory();
        }
    }
}
