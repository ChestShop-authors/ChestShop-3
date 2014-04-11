package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class WorldGuardBuilding implements Listener {
    private WorldGuardPlugin worldGuard;

    public WorldGuardBuilding(WorldGuardPlugin plugin) {
        this.worldGuard = plugin;
    }

    @EventHandler
    public void canBuild(BuildPermissionEvent event) {
        ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());

        if (Properties.WORLDGUARD_USE_FLAG) {
            event.allow(regions.allows(DefaultFlag.ENABLE_SHOP));
        } else {
            event.allow(regions.size() != 0);
        }
    }

    private ApplicableRegionSet getApplicableRegions(Location location) {
        return worldGuard.getGlobalRegionManager().get(location.getWorld()).getApplicableRegions(BukkitUtil.toVector(location));
    }
}
