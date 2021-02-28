package com.Acrobot.ChestShop.Plugins;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;

import nl.rutgerkok.blocklocker.BlockLockerAPIv2;

/**
 * @author Acrobot
 */
public class BlockLocker implements Listener {


    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        if (BlockLockerAPIv2.isProtected(block) && !BlockLockerAPIv2.isOwner(event.getPlayer(), block)) {
            event.setResult(Event.Result.DENY);
        }
    }
}
