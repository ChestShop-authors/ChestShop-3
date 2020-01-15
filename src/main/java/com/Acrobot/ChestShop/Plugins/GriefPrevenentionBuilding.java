package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * @author Acrobot
 */
public class GriefPrevenentionBuilding implements Listener {
    private GriefPrevention griefPrevention;

    public GriefPrevenentionBuilding(Plugin plugin) {
        this.griefPrevention = (GriefPrevention) plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void canBuild(BuildPermissionEvent event) {
        event.allow(griefPrevention.dataStore.getClaimAt(event.getSign(), false, null) != null);
    }
}
