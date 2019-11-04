package com.Acrobot.ChestShop.Listeners.Block.Break.Attached;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import static com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak.handlePhysicsBreak;

public class PhysicsBreak implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public static void onSign(BlockPhysicsEvent event) {
        handlePhysicsBreak(event.getBlock());
    }
}
