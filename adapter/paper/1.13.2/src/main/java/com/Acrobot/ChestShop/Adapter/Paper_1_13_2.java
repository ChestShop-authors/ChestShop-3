package com.Acrobot.ChestShop.Adapter;

import com.Acrobot.ChestShop.Utils.VersionAdapter;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak.handlePhysicsBreak;

public class Paper_1_13_2 implements Listener, VersionAdapter {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public static void onSign(BlockDestroyEvent event) {
        handlePhysicsBreak(event.getBlock());
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("com.destroystokyo.paper.event.block.BlockDestroyEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
