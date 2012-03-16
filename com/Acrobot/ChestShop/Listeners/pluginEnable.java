package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy.NoProvider;
import com.Acrobot.ChestShop.Economy.Register;
import com.Acrobot.ChestShop.Economy.Vault;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Protection.Plugins.*;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.WorldGuard.uWorldGuard;
import com.Acrobot.ChestShop.Utils.uHeroes;
import com.Acrobot.ChestShop.Utils.uSign;
import com.daemitus.deadbolt.Deadbolt;
import com.griefcraft.lwc.LWCPlugin;
import com.herocraftonline.dev.heroes.Heroes;
import com.nijikokun.register.payment.forChestShop.Method;
import com.nijikokun.register.payment.forChestShop.Methods;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.webkonsept.bukkit.simplechestlock.SCL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class pluginEnable {

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
            if (m == null) {
                com.Acrobot.ChestShop.Economy.Economy.economy = new NoProvider();
                return;
            }
            Register.eco = m;
            com.Acrobot.ChestShop.Economy.Economy.economy = new Register();
            System.out.println(ChestShop.chatPrefix + m.getName() + " version " + m.getVersion() + " loaded.");
        }
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        if (name.equals("LWC")) {
            LWCplugin.setLWC(((LWCPlugin) plugin).getLWC());
            Security.protections.add(new LWCplugin());
        } else if (name.equals("Lockette")) {
            LockettePlugin.lockette = (Lockette) plugin;
            Security.protections.add(new LockettePlugin());
        } else if (name.equals("Deadbolt")) {
            Security.protections.add(new DeadboltPlugin());
        } else if (name.equals("OddItem")) {
            Odd.isInitialized = true;
        } else if (name.equals("Towny")) {
            uSign.towny = (Towny) plugin;
        } else if (name.equals("WorldGuard")) {
            uWorldGuard.wg = (WorldGuardPlugin) plugin;
            uWorldGuard.injectHax(); //Inject hax into WorldGuard
        } else if (name.equals("Vault")) {
            if (com.Acrobot.ChestShop.Economy.Economy.economy != null) return;
            RegisteredServiceProvider<Economy> rsp = ChestShop.getBukkitServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) return;
            Vault.economy = rsp.getProvider();
            if (Vault.economy == null) return;
            com.Acrobot.ChestShop.Economy.Economy.economy = new Vault();
            System.out.println(ChestShop.chatPrefix + "Vault loaded using economy plugin " + Vault.economy.getName());
            return;
        } else if (name.equals("Heroes")){
            uHeroes.heroes = (Heroes) plugin;
        } else if (name.equals("SimpleChestLock")) {
            SCLplugin.scl = (SCL) plugin;
            Security.protections.add(new SCLplugin());
        } else {
            return;
        }
        
        PluginDescriptionFile description = plugin.getDescription();
        System.out.println(ChestShop.chatPrefix + description.getName() + " version " + description.getVersion() + " loaded.");
    }

    private static String generateOutdatedVersion(String pluginName, String curVersion, String neededVersion) {
        return (new StringBuilder(7).append(ChestShop.chatPrefix).append("Your ").append(pluginName).append(" is outdated! Need version AT LEAST ").append(neededVersion).append(" - Your version is ").append(curVersion).toString());
    }
}
