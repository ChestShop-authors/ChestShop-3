package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Economy.NoProvider;
import com.Acrobot.ChestShop.Economy.Register;
import com.Acrobot.ChestShop.Economy.Vault;
import com.Acrobot.ChestShop.Plugins.*;
import com.nijikokun.register.payment.forChestShop.Method;
import com.nijikokun.register.payment.forChestShop.Methods;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

/**
 * @author Acrobot
 */
public class Dependencies {
    public static void load() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        for (String dependency : ChestShop.getDependencies()) {
            Plugin plugin = pluginManager.getPlugin(dependency);

            if (plugin != null) {
                initializePlugin(dependency, plugin);
            }
        }

        if (!Economy.isLoaded()) {
            loadRegister();
        }
    }

    private static void loadRegister() {
        Method method = Methods.load();

        if (method == null) {
            Economy.setPlugin(new NoProvider());
            return;
        }

        Economy.setPlugin(new Register(method));

        ChestShop.getBukkitLogger().info(method.getName() + " version " + method.getVersion() + " loaded.");
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        Dependency dependency;

        try {
            dependency = Dependency.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return;
        }

        Listener listener = null;

        switch(dependency) {
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
                listener = new ResidenceChestProtection();
                break;

            //Terrain protection plugins
            case Towny:
                Towny towny = Towny.getTowny();

                if (towny == null || !Properties.TOWNY_INTEGRATION) {
                    return;
                }

                listener = towny;

                break;
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
            case Vault:
                Vault vault = Vault.getVault();

                if (vault == null) {
                    return;
                }

                Economy.setPlugin(vault);

                ChestShop.getBukkitLogger().info("Vault loaded - using " + Vault.getPluginName());
                return;
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

        Towny,
        WorldGuard,

        Vault,
        Heroes
    }
}
