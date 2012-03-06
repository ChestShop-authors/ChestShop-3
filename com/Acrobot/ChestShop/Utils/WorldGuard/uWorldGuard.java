package com.Acrobot.ChestShop.Utils.WorldGuard;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.Location;

/**
 * @author Acrobot
 */
public class uWorldGuard {
    public static WorldGuardPlugin wg;

    public static void injectHax() {
        if (!Config.getBoolean(Property.WORLDGUARD_INTEGRATION) || wg == null) return;

        JavaWorkaround.injectHax();
    }

    public static boolean canBuildShopHere(Location loc) {
        return !turnedOn() || canCreateShops(getRegions(loc));
    }

    public static boolean canCreateShops(ApplicableRegionSet set) {
        return JavaWorkaround.setAllowsFlag(set);
    }

    public static ApplicableRegionSet getRegions(Location loc) {
        return wg.getGlobalRegionManager().get(loc.getWorld()).getApplicableRegions(BukkitUtil.toVector(loc));
    }

    private static boolean turnedOn() {
        return wg != null && Config.getBoolean(Property.WORLDGUARD_INTEGRATION);
    }
}
