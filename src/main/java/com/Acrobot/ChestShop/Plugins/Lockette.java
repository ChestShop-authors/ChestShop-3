package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
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

        Account account = NameManager.getAccount(player.getUniqueId());
        if (account != null && !org.yi.acru.bukkit.Lockette.Lockette.isUser(block, account.getName(), true)) {
            event.setResult(Event.Result.DENY);
        }
    }
}
