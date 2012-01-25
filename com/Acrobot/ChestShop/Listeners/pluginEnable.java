package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy.Register;
import com.Acrobot.ChestShop.Economy.Vault;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Plugins.DeadboltPlugin;
import com.Acrobot.ChestShop.Protection.Plugins.Default;
import com.Acrobot.ChestShop.Protection.Plugins.LWCplugin;
import com.Acrobot.ChestShop.Protection.Plugins.LockettePlugin;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.uHeroes;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.Acrobot.ChestShop.Utils.uSign;
import com.Acrobot.ChestShop.Utils.uWorldGuard;
import com.daemitus.deadbolt.Deadbolt;
import com.griefcraft.lwc.LWCPlugin;
import com.herocraftonline.dev.heroes.Heroes;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.forChestShop.Method;
import com.nijikokun.register.payment.forChestShop.Methods;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class pluginEnable extends ServerListener {

    public static void initializePlugins() {
        Security.protections.add(new Default()); //Initialize basic protection
        for (Object plugin : ChestShop.getDependencies()) {
            Plugin pl = ChestShop.pm.getPlugin((String) plugin);
            if (pl != null) initializePlugin((String) plugin, pl);
        }
        loadRegister();
    }
    
    private static void loadRegister(){
        if (com.Acrobot.ChestShop.Economy.Economy.economy == null) {
            Method m = Methods.load(ChestShop.pm);
            if (m == null) return;
            Register.eco = m;
            com.Acrobot.ChestShop.Economy.Economy.economy = new Register();
            System.out.println(ChestShop.chatPrefix + m.getName() + " loaded.");
        }
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        if (name.equals("Permissions")) {
            Permission.permissions = ((Permissions) plugin).getHandler();
        } else if (name.equals("LWC")) {
            LWCplugin.setLWC(((LWCPlugin) plugin).getLWC());
            Security.protections.add(new LWCplugin());
        } else if (name.equals("Lockette")) {
            LockettePlugin.lockette = (Lockette) plugin;
            Security.protections.add(new LockettePlugin());
        } else if (name.equals("Deadbolt")) {
            DeadboltPlugin.deadbolt = (Deadbolt) plugin;
            Security.protections.add(new DeadboltPlugin());
        } else if (name.equals("OddItem")) {
            if (Odd.isInitialized()) return;
            if (plugin.getDescription().getVersion().startsWith("0.7")) { System.out.println(generateOutdatedVersion(name, plugin.getDescription().getVersion(), "0.8")); return; }
            Odd.isInitialized = true;
        } else if (name.equals("Towny")) {
            int versionNumber = 0;
            String[] split = plugin.getDescription().getVersion().split("\\.");
            for (int i = 0; i < 4; i++) if (split.length >= i + 1 && uNumber.isInteger(split[i])) versionNumber += (Math.pow(10, (3 - i) << 1) * Integer.parseInt(split[i])); //EPIC CODE RIGHT HERE
            if (versionNumber < 760047) { System.out.println(generateOutdatedVersion(name, plugin.getDescription().getVersion(), "0.76.0.47")); return; }
            uSign.towny = (Towny) plugin;
        } else if (name.equals("WorldGuard")) {
            uWorldGuard.worldGuard = (WorldGuardPlugin) plugin;
        } else if (name.equals("Vault")) {
            if (com.Acrobot.ChestShop.Economy.Economy.economy != null) return;
            RegisteredServiceProvider<Economy> rsp = ChestShop.getBukkitServer().getServicesManager().getRegistration(Economy.class);
            Vault.economy = rsp.getProvider();
            if (Vault.economy == null) return;
            com.Acrobot.ChestShop.Economy.Economy.economy = new Vault();
            System.out.println(ChestShop.chatPrefix + "Vault loaded using economy plugin " + Vault.economy.getName());
            return;
        } else if (name.equals("Heroes")){
            uHeroes.heroes = (Heroes) plugin;
        }
        
        PluginDescriptionFile description = plugin.getDescription();
        System.out.println(ChestShop.chatPrefix + description.getName() + " version " + description.getVersion() + " loaded.");
    }

    private static String generateOutdatedVersion(String pluginName, String curVersion, String neededVersion) {
        return (new StringBuilder(7).append(ChestShop.chatPrefix).append("Your ").append(pluginName).append(" is outdated! Need version AT LEAST ").append(neededVersion).append(" - Your version is ").append(curVersion).toString());
    }
}
