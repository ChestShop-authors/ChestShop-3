package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class Deadbolt implements Listener {
    @EventHandler
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!com.daemitus.deadbolt.Deadbolt.isProtected(block)) {
            return;
        }

        String shortPlayerName = uName.shortenName(player);

        if (!com.daemitus.deadbolt.Deadbolt.getAllNames(block).contains(shortPlayerName)) {
            event.setResult(Event.Result.DENY);
        }
    }
}
