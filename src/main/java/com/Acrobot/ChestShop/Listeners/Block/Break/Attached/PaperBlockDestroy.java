package com.Acrobot.ChestShop.Listeners.Block.Break.Attached;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak.handlePhysicsBreak;

public class PaperBlockDestroy implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public static void onSign(BlockDestroyEvent event) {
        handlePhysicsBreak(event.getBlock());
    }
}
