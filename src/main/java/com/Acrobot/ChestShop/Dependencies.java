package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Listeners.Economy.EconomyAdapter;
import com.Acrobot.ChestShop.Listeners.Economy.Plugins.ReserveListener;
import com.Acrobot.ChestShop.Listeners.Economy.Plugins.VaultListener;
import com.Acrobot.ChestShop.Plugins.*;
import com.google.common.collect.ImmutableMap;
import org.bstats.charts.DrilldownPie;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Acrobot
 */
public class Dependencies implements Listener {

    private static final Map<String, String> versions = new HashMap<>();

    public static void initializePlugins() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        for (String dependency : ChestShop.getDependencies()) {
            Plugin plugin = pluginManager.getPlugin(dependency);

            if (plugin != null) {
                initializePlugin(dependency, plugin);
            }
        }
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        Dependency dependency;

        try {
            dependency = Dependency.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return;
        }

        switch (dependency) {
            //Terrain protection plugins
            case WorldGuard:
                WorldGuardFlags.ENABLE_SHOP.getName();  // force the static code to run
                break;
        }

        PluginDescriptionFile description = plugin.getDescription();
        ChestShop.getBukkitLogger().info(description.getName() + " version " + description.getVersion() + " loaded.");
    }

    public static boolean loadPlugins() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        for (String dependency : ChestShop.getDependencies()) {
            Plugin plugin = pluginManager.getPlugin(dependency);

            if (plugin != null && plugin.isEnabled()) {
                try {
                    loadPlugin(dependency, plugin);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Unable to hook into " + plugin.getName() + " " + plugin.getDescription().getVersion(), e);
                }
            }
        }

        if (loadEconomy()) {
            Map<String, Map<String, Integer>> map = versions.entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<String, Map<String, Integer>>(e.getKey(), ImmutableMap.of(e.getValue(), 1)))
                    .collect(Collectors.toMap(
                            AbstractMap.SimpleEntry::getKey,
                            AbstractMap.SimpleEntry::getValue
                    ));
            ChestShop.getMetrics().addCustomChart(new DrilldownPie("dependencies", () -> map));
            return true;
        }
        return false;
    }

    private static boolean loadEconomy() {
        String plugin = "none";

        EconomyAdapter economy = null;

        if(Bukkit.getPluginManager().getPlugin("Reserve") != null) {
            plugin = "Reserve";
            economy = ReserveListener.prepareListener();
        }

        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
            plugin = "Vault";
            economy = VaultListener.initializeVault();
        }

        if (economy == null) {
            ChestShop.getBukkitLogger().severe("No Economy adapter found! You need to install either Vault or Reserve!");
            return false;
        }

        ChestShop.getMetrics().addCustomChart(ChestShop.createStaticDrilldownStat("economyAdapter", plugin, Bukkit.getPluginManager().getPlugin(plugin).getDescription().getVersion()));
        ChestShop.getMetrics().addCustomChart(ChestShop.createStaticDrilldownStat("economyPlugin", economy.getProviderInfo().getName(), economy.getProviderInfo().getVersion()));

        ChestShop.registerListener(economy);
        ChestShop.getBukkitLogger().info(plugin + " loaded!");
        return true;
    }

    private static void loadPlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
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
            case LockettePro:
                listener = new LockettePro();
                break;
            case Deadbolt:
                listener = new Deadbolt();
                break;
            case SimpleChestLock:
                listener = SimpleChestLock.getSimpleChestLock(plugin);
                break;
            case BlockLocker:
                listener = new BlockLocker();
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
                boolean inUse = Properties.WORLDGUARD_USE_PROTECTION || Properties.WORLDGUARD_INTEGRATION;

                if (!inUse) {
                    return;
                }

                if (Properties.WORLDGUARD_USE_PROTECTION) {
                    ChestShop.registerListener(new WorldGuardProtection(plugin));
                }

                if (Properties.WORLDGUARD_INTEGRATION) {
                    listener = new WorldGuardBuilding(plugin);
                }

                break;

            case GriefPrevention:
                if (!Properties.GRIEFPREVENTION_INTEGRATION) {
                    return;
                }
                listener = new GriefPrevenentionBuilding(plugin);
                break;

            case RedProtect:
                if (!Properties.REDPROTECT_INTEGRATION) {
                    return;
                }
                listener = new RedProtectBuilding(plugin);
                break;

            //Other plugins
            case Heroes:
                Heroes heroes = Heroes.getHeroes(plugin);

                if (heroes == null) {
                    return;
                }

                listener = heroes;
                break;
            case ItemBridge:
                listener = new ItemBridge();
                break;
            case ShowItem:
                MaterialUtil.Show.initialize(plugin);
                break;
        }

        if (listener != null) {
            ChestShop.registerListener(listener);
        }

        PluginDescriptionFile description = plugin.getDescription();
        versions.put(description.getName(), description.getVersion());
        ChestShop.getBukkitLogger().info(description.getName() + " version " + description.getVersion() + " loaded.");
    }

    private static enum Dependency {
        LWC,
        Lockette,
        LockettePro,
        Deadbolt,
        SimpleChestLock,
        BlockLocker,
        Residence,

        WorldGuard,
        GriefPrevention,
        RedProtect,

        Heroes,

        ItemBridge,

        ShowItem
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (ChestShop.getDependencies().contains(plugin.getName())) {
            loadPlugin(plugin.getName(), plugin);
        }
    }
}
