package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LockettePro implements Listener {

    @EventHandler
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!LocketteProAPI.isProtected(block)) {
            return;
        }
        
        if (LocketteProAPI.isLocked(block) && !LocketteProAPI.isOwner(block, player)) {
            event.setResult(Event.Result.DENY);
        }
    }
}
