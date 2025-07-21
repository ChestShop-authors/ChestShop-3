package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * @author Acrobot
 */
public class WorldGuardBuilding implements Listener {
    private WorldGuardPlugin worldGuard;
    private WorldGuardPlatform worldGuardPlatform;

    public WorldGuardBuilding(Plugin plugin) {
        this.worldGuard = (WorldGuardPlugin) plugin;
        this.worldGuardPlatform = WorldGuard.getInstance().getPlatform();
    }

    @EventHandler(ignoreCancelled = true)
    public void canBuild(BuildPermissionEvent event) {
        ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());

        if (regions == null) {
            event.allow(false);
        } else if (Properties.WORLDGUARD_USE_FLAG) {
            event.allow(regions.queryState(worldGuard.wrapPlayer(event.getPlayer()), WorldGuardFlags.ENABLE_SHOP) == StateFlag.State.ALLOW);
        } else {
            event.allow(regions.size() > 0);
        }
    }

    private ApplicableRegionSet getApplicableRegions(Location location) {
        RegionManager regionManager = worldGuardPlatform.getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager == null) {
            return null;
        }
        return regionManager.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
    }
}
