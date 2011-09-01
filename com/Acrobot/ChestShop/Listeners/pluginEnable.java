package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.LWCplugin;
import com.Acrobot.ChestShop.Protection.LockettePlugin;
import com.Acrobot.ChestShop.Protection.Security;
import com.daemitus.lockette.Lockette;
import com.griefcraft.lwc.LWCPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.forChestShop.Methods;
import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Acrobot
 */
public class pluginEnable extends ServerListener {

    public static final Methods methods = new Methods(Config.getPreferred());
    private static final String lineStart = "[ChestShop] ";

    private static final List<String> pluginsToLoad = new LinkedList<String>(Arrays.asList(
            "Permissions",
            "LWC",
            "Lockette",
            "OddItem"
    ));

    public void onPluginEnable(PluginEnableEvent event) {
        LinkedList<String> toRemove = new LinkedList();

        //Economy plugins
        if (!methods.hasMethod()) {
            if (methods.setMethod(event.getPlugin())) {
                Economy.economy = methods.getMethod();
                System.out.println(lineStart + Economy.economy.getName() + ' ' + Economy.economy.getVersion() + " loaded.");
            }
        }

        for (String pluginName : pluginsToLoad) {
            Plugin plugin = ChestShop.getBukkitServer().getPluginManager().getPlugin(pluginName);
            if (plugin == null) continue;
            initializePlugin(pluginName, plugin);
            toRemove.add(pluginName);
        }

        for (String pluginName : toRemove) pluginsToLoad.remove(pluginName);
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        PluginDescriptionFile description = plugin.getDescription();
        if (name.equals("Permissions")) {
            if (Permission.permissions != null) return;
            Permission.permissions = ((Permissions) plugin).getHandler();
        } else if (name.equals("LWC")) {
            if (LWCplugin.lwc != null) return;
            LWCplugin.setLWC(((LWCPlugin) plugin).getLWC());
            Security.protection = new LWCplugin();
        } else if (name.equals("Lockette")) {
            if (LockettePlugin.lockette != null) return;
            LockettePlugin.lockette = (Lockette) plugin;
            Security.protection = new LockettePlugin();
        } else if (name.equals("OddItem")) {
            if (Odd.oddItem != null) return;
            Odd.oddItem = (OddItem) plugin;
        }
        System.out.println(lineStart + description.getName() + " version " + description.getVersion() + " loaded.");
    }
}
