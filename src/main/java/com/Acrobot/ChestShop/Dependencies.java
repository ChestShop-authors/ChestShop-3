package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Listeners.Economy.Plugins.ReserveListener;
import com.Acrobot.ChestShop.Listeners.Economy.Plugins.VaultListener;
import com.Acrobot.ChestShop.Plugins.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.tnemc.core.Reserve;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

/**
 * @author Acrobot
 */
public class Dependencies {
    public static boolean loadPlugins() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        for (String dependency : ChestShop.getDependencies()) {
            Plugin plugin = pluginManager.getPlugin(dependency);

            if (plugin != null) {
                initializePlugin(dependency, plugin);
            }
        }

        return loadEconomy();
    }

    private static boolean loadEconomy() {
        String plugin = "none";

        Listener economy = null;

        if(Bukkit.getPluginManager().getPlugin("Reserve") != null) {
            plugin = "Reserve";
            economy = ReserveListener.prepareListener();
        }

        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
            plugin = "Vault";
            economy = VaultListener.initializeVault();
        }

        if (economy == null) {
            ChestShop.getBukkitLogger().severe("No Economy plugin found! You need to install either Vault or Reserve and a compatible economy!");
            return false;
        }

        ChestShop.registerListener(economy);
        ChestShop.getBukkitLogger().info(plugin + " loaded! Found an economy plugin!");
        return true;
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        Dependency dependency;

        try {
            dependency = Dependency.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return;
        }

        Listener listener = null;

        switch (dependency) {
            //Protection plugins
            case LWC:
                listener = new LightweightChestProtection();
                break;
            case Lockette:
                listener = new Lockette();
                break;
            case Deadbolt:
                listener = new Deadbolt();
                break;
            case SimpleChestLock:
                listener = SimpleChestLock.getSimpleChestLock(plugin);
                break;
            case Residence:
                if (plugin.getDescription().getVersion().startsWith("2")) {
                    ChestShop.getBukkitLogger().severe("You are using an old version of Residence! " +
                            "Please update to the newest one, which supports UUIDs: http://ci.drtshock.net/job/Residence/");

                    break;
                }

                listener = new ResidenceChestProtection();
                break;

            //Terrain protection plugins
            case WorldGuard:
                WorldGuardPlugin worldGuard = (WorldGuardPlugin) plugin;
                boolean inUse = Properties.WORLDGUARD_USE_PROTECTION || Properties.WORLDGUARD_INTEGRATION;

                if (!inUse) {
                    return;
                }

                if (Properties.WORLDGUARD_USE_PROTECTION) {
                    ChestShop.registerListener(new WorldGuardProtection(worldGuard));
                }

                if (Properties.WORLDGUARD_INTEGRATION) {
                    listener = new WorldGuardBuilding(worldGuard);
                }

                break;

            //Other plugins
            case Heroes:
                Heroes heroes = Heroes.getHeroes(plugin);

                if (heroes == null) {
                    return;
                }

                listener = heroes;
                break;
            case OddItem:
                MaterialUtil.Odd.initialize();
                break;
            case ShowItem:
                MaterialUtil.Show.initialize(plugin);
                break;
        }

        if (listener != null) {
            ChestShop.registerListener(listener);
        }

        PluginDescriptionFile description = plugin.getDescription();
        ChestShop.getBukkitLogger().info(description.getName() + " version " + description.getVersion() + " loaded.");
    }

    private static enum Dependency {
        LWC,
        Lockette,
        Deadbolt,
        SimpleChestLock,
        Residence,

        OddItem,

        WorldGuard,

        Heroes,

        ShowItem
    }
}
