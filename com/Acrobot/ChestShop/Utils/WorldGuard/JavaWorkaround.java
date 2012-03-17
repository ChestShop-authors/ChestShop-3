package com.Acrobot.ChestShop.Utils.WorldGuard;

import com.Acrobot.ChestShop.ChestShop;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Acrobot
 */
public class JavaWorkaround {
    public static boolean setAllowsFlag(ApplicableRegionSet set) {
        return set.allows(ChestShopFlag.flag);
    }

    private static List elements() {
        List<Flag> elements = new ArrayList(Arrays.asList(DefaultFlag.getFlags()));
        elements.add(ChestShopFlag.flag);
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
            System.err.println(ChestShop.chatPrefix + "Oh noes! Something wrong happened! Be sure to paste that in your bug report:");
            e.printStackTrace();
        }
    }
}
