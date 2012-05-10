package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Events.BuildPermissionEvent;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Acrobot
 */
public class WorldGuardBuilding implements Listener {
    private WorldGuardPlugin worldGuard;

    public WorldGuardBuilding(WorldGuardPlugin plugin) {
        this.worldGuard = plugin;

        if (Config.getBoolean(Property.WORLDGUARD_USE_FLAG)) {
            ChestShopFlag.injectHax();
        }
    }

    @EventHandler
    public void canBuild(BuildPermissionEvent event) {
        ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());

        if (Config.getBoolean(Property.WORLDGUARD_USE_FLAG)) {
            event.allow(ChestShopFlag.setAllowsFlag(regions));
        } else {
            event.allow(regions.size() != 0);
        }
    }

    private ApplicableRegionSet getApplicableRegions(Location location) {
        return worldGuard.getGlobalRegionManager().get(location.getWorld()).getApplicableRegions(BukkitUtil.toVector(location));
    }

    public static class ChestShopFlag extends StateFlag {
        public static ChestShopFlag flag = new ChestShopFlag();

        public ChestShopFlag() {
            super("chestshop", false);
        }

        public static boolean setAllowsFlag(ApplicableRegionSet set) {
            return set.allows(flag);
        }

        private static List elements() {
            List<Flag> elements = new ArrayList(Arrays.asList(DefaultFlag.getFlags()));
            elements.add(flag);
            return elements;
        }

        public static void injectHax() {
            try {
                Field field = DefaultFlag.class.getDeclaredField("flagsList");

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                field.setAccessible(true);

                List<Flag> elements = elements();

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
                ChestShop.getBukkitLogger().severe("Oh noes! Something wrong happened! Be sure to paste that in your bug report:");
                e.printStackTrace();
            }
        }
    }
}
