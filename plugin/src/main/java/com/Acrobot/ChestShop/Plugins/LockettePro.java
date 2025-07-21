package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LockettePro implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY && !Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!LocketteProAPI.isProtected(block) && !LocketteProAPI.isLocked(block)) {
            return;
        }
        
        if (!LocketteProAPI.isUser(block, player)) {
            event.setResult(Event.Result.DENY);
        } else if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
