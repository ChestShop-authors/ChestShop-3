package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Configuration.Properties;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;

import nl.rutgerkok.blocklocker.BlockLockerAPIv2;

/**
 * @author Acrobot
 */
public class BlockLocker implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY && !Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            return;
        }

        Block block = event.getBlock();
        if (!BlockLockerAPIv2.isProtected(block)) {
            return;
        }

        if (!BlockLockerAPIv2.isOwner(event.getPlayer(), block)) {
            event.setResult(Event.Result.DENY);
        } else if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}
