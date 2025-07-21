package com.Acrobot.ChestShop.Adapter;

import com.Acrobot.ChestShop.Utils.VersionAdapter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.logging.Level;

import static com.Acrobot.ChestShop.Dependencies.loadPlugin;

public class Spigot_1_15_2 implements Listener, VersionAdapter {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        try {
            for (String pluginAlias : plugin.getDescription().getProvides()) {
                if (loadPlugin(pluginAlias, plugin)) {
                    break;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Unable to hook into " + plugin.getName() + " " + plugin.getDescription().getVersion(), e);
        }
    }

    @Override
    public boolean isSupported() {
        try {
            PluginDescriptionFile.class.getMethod("getProvides");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
