package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Plugins.DeadboltPlugin;
import com.Acrobot.ChestShop.Protection.Plugins.LWCplugin;
import com.Acrobot.ChestShop.Protection.Plugins.LockettePlugin;
import com.Acrobot.ChestShop.Protection.Security;
import com.LRFLEW.register.payment.forChestShop.Methods;
import com.daemitus.deadbolt.Deadbolt;
import com.griefcraft.lwc.LWCPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class pluginEnable extends ServerListener {

    public void onPluginEnable(PluginEnableEvent event) {
        if (!Methods.hasMethod() && Methods.setMethod(ChestShop.pm)) {
            Economy.economy = Methods.getMethod();
            System.out.println(ChestShop.chatPrefix + Economy.economy.getName() + ' ' + Economy.economy.getVersion() + " loaded.");
        }
    }

    public static void initializePlugins() {
        for (Object plugin : ChestShop.getDependencies()) {
            Plugin pl = ChestShop.pm.getPlugin((String) plugin);
            if (pl != null) initializePlugin((String) plugin, pl);
        }
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
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
        } else if (name.equals("Deadbolt")) {
            if (DeadboltPlugin.deadbolt != null) return;
            DeadboltPlugin.deadbolt = (Deadbolt) plugin;
            Security.protection = new DeadboltPlugin();
        } else if (name.equals("OddItem")) {
            if (Odd.oddItem != null) return;
            Odd.oddItem = (OddItem) plugin;
        }
        PluginDescriptionFile description = plugin.getDescription();
        System.out.println(ChestShop.chatPrefix + description.getName() + " version " + description.getVersion() + " loaded.");
    }
}
