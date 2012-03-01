package com.Acrobot.ChestShop.Utils.WorldGuard;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Acrobot
 */
public class uWorldGuard {
    public static WorldGuardPlugin worldGuard;
    private static final ChestShopFlag flag = new ChestShopFlag();

    /*public static boolean isNotOutsideWGplot(Location l2) {
        return worldGuard == null || !Config.getBoolean(Property.WORLDGUARD_INTEGRATION) || worldGuard.getGlobalRegionManager().get(l2.getWorld()).getApplicableRegions(BukkitUtil.toVector(l2)).size() != 0;
    }*/

    public static void injectHax() {
        if (!Config.getBoolean(Property.WORLDGUARD_INTEGRATION)) return;

        try {
            Field field = DefaultFlag.class.getDeclaredField("flagsList");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.setAccessible(true);

            List<Flag> elements = new ArrayList(Arrays.asList(DefaultFlag.getFlags()));
            elements.add(flag);

            Flag<?> list[] = new Flag<?>[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                list[i] = elements.get(i);
            }

            field.set(null, list);
            
            Field grm = WorldGuardPlugin.class.getDeclaredField("globalRegionManager");
            grm.setAccessible(true);
            GlobalRegionManager globalRegionManager = (GlobalRegionManager) grm.get(ChestShop.getBukkitServer().getPluginManager().getPlugin("WorldGuard"));

            globalRegionManager.preload();

        } catch (Exception e) {
            System.out.println(ChestShop.chatPrefix + "Oh noes! Something wrong happened! Be sure to paste that in your bug report:");
            e.printStackTrace();
        }
    }
    
    public static boolean canBuildShopHere(Location loc) {
        return turnedOn() && canCreateShops(getRegions(loc));
    }

    public static boolean canCreateShops(ApplicableRegionSet set){
        return set.allows(flag);
    }
    
    public static ApplicableRegionSet getRegions(Location loc) {
        return worldGuard.getGlobalRegionManager().get(loc.getWorld()).getApplicableRegions(BukkitUtil.toVector(loc));
    }

    private static boolean turnedOn() {
        return worldGuard != null && Config.getBoolean(Property.WORLDGUARD_INTEGRATION);
    }

    private static class ChestShopFlag extends StateFlag {
        public ChestShopFlag() {
            super("chestshop", false);
        }
    }
}
