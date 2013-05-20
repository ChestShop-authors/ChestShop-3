package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class Lockette implements Listener {
    @EventHandler
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!org.yi.acru.bukkit.Lockette.Lockette.isProtected(block)) {
            return;
        }

        String shortPlayerName = uName.stripName(player);

        if (!org.yi.acru.bukkit.Lockette.Lockette.isUser(block, shortPlayerName, true)) {
            event.setResult(Event.Result.DENY);
        }
    }
}
