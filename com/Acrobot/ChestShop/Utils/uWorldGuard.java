package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Location;

/**
 * @author Acrobot
 */
public class uWorldGuard {
    public static WorldGuardPlugin worldGuard;

    public static boolean isNotOutsideWGplot(Location l2) {
        return worldGuard == null || !Config.getBoolean(Property.WORLDGUARD_INTEGRATION) || worldGuard.getGlobalRegionManager().get(l2.getWorld()).getApplicableRegions(BukkitUtil.toVector(l2)).size() != 0;
    }
}
