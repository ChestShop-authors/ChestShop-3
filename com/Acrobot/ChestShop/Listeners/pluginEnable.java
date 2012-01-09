package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Plugins.DeadboltPlugin;
import com.Acrobot.ChestShop.Protection.Plugins.Default;
import com.Acrobot.ChestShop.Protection.Plugins.LWCplugin;
import com.Acrobot.ChestShop.Protection.Plugins.LockettePlugin;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.Acrobot.ChestShop.Utils.uSign;
import com.Acrobot.ChestShop.Utils.uWorldGuard;
import com.daemitus.deadbolt.Deadbolt;
import com.griefcraft.lwc.LWCPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.forChestShop.Methods;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
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
        Security.protections.add(new Default()); //Initialize basic protection
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
            Security.protections.add(new LWCplugin());
        } else if (name.equals("Lockette")) {
            if (LockettePlugin.lockette != null) return;
            LockettePlugin.lockette = (Lockette) plugin;
            Security.protections.add(new LockettePlugin());
        } else if (name.equals("Deadbolt")) {
            if (DeadboltPlugin.deadbolt != null) return;
            DeadboltPlugin.deadbolt = (Deadbolt) plugin;
            Security.protections.add(new DeadboltPlugin());
        } else if (name.equals("OddItem")) {
            if (Odd.isInitialized()) return;
            if (plugin.getDescription().getVersion().startsWith("0.7")) { System.out.println(generateOutdatedVersion(name, plugin.getDescription().getVersion(), "0.8")); return; }
            Odd.isInitialized = true;
        } else if (name.equals("Towny")) {
            if (uSign.towny != null) return;
            int versionNumber = 0;
            String[] split = plugin.getDescription().getVersion().split("\\.");
            for (int i = 0; i < 4; i++) if (split.length >= i + 1 && uNumber.isInteger(split[i])) versionNumber += (Math.pow(10, (3 - i) << 1) * Integer.parseInt(split[i])); //EPIC CODE RIGHT HERE
            if (versionNumber < 760047) { System.out.println(generateOutdatedVersion(name, plugin.getDescription().getVersion(), "0.76.0.47")); return; }
            uSign.towny = (Towny) plugin;
        } else if (name.equals("WorldGuard")) {
            if (uWorldGuard.worldGuard != null) return;
            uWorldGuard.worldGuard = (WorldGuardPlugin) plugin;
        }
        PluginDescriptionFile description = plugin.getDescription();
        System.out.println(ChestShop.chatPrefix + description.getName() + " version " + description.getVersion() + " loaded.");
    }

    private static String generateOutdatedVersion(String pluginName, String curVersion, String neededVersion) {
        return (new StringBuilder(7).append(ChestShop.chatPrefix).append("Your ").append(pluginName).append(" is outdated! Need version AT LEAST ").append(neededVersion).append(" - Your version is ").append(curVersion).toString());
    }
}
