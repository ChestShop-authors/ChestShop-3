package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Economy.NoProvider;
import com.Acrobot.ChestShop.Economy.Register;
import com.Acrobot.ChestShop.Economy.Vault;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Protection.Plugins.*;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.WorldGuard.uWorldGuard;
import com.Acrobot.ChestShop.Utils.uHeroes;
import com.Acrobot.ChestShop.Utils.uSign;
import com.griefcraft.lwc.LWCPlugin;
import com.herocraftonline.heroes.Heroes;
import com.nijikokun.register.payment.forChestShop.Method;
import com.nijikokun.register.payment.forChestShop.Methods;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.webkonsept.bukkit.simplechestlock.SCL;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class pluginEnable {

    public static void initializePlugins() {
        Security.protections.add(Security.getDefaultProtection());

        for (Object plugin : ChestShop.getDependencies()) {
            Plugin pl = ChestShop.pm.getPlugin((String) plugin);
            if (pl != null) {
                initializePlugin((String) plugin, pl);
            }
        }
        loadRegister();
    }

    private static void loadRegister() {
        if (Economy.economy != null) {
            return;
        }

        Method method = Methods.load(ChestShop.pm);
        if (method == null) {
            Economy.economy = new NoProvider();
            return;
        }
        Economy.economy = new Register(method);
        System.out.println(ChestShop.chatPrefix + method.getName() + " version " + method.getVersion() + " loaded.");
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        if (name.equals("LWC")) {
            Security.protections.add(new LWCplugin(((LWCPlugin) plugin).getLWC()));
        } else if (name.equals("Lockette")) {
            Security.protections.add(new LockettePlugin((Lockette) plugin));
        } else if (name.equals("Deadbolt")) {
            Security.protections.add(new DeadboltPlugin());
        } else if (name.equals("OddItem")) {
            Odd.isInitialized = true;
        } else if (name.equals("Towny")) {
            uSign.towny = (Towny) plugin;
        } else if (name.equals("WorldGuard")) {
            Security.protections.add(new WorldGuardProtectionPlugin((WorldGuardPlugin) plugin));
            uWorldGuard.wg = (WorldGuardPlugin) plugin;
            uWorldGuard.injectHax(); //Inject hax into WorldGuard
        } else if (name.equals("Vault")) {
            if (Economy.economy != null) return;

            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = ChestShop.getBukkitServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (rsp == null) return;

            Vault.economy = rsp.getProvider();
            if (Vault.economy == null) return;

            Economy.economy = new Vault();
            System.out.println(ChestShop.chatPrefix + "Vault loaded using economy plugin " + Vault.economy.getName());
            return;
        } else if (name.equals("Heroes")) {
            uHeroes.heroes = (Heroes) plugin;
        } else if (name.equals("SimpleChestLock")) {
            Security.protections.add(new SCLplugin((SCL) plugin));
        } else {
            return;
        }

        PluginDescriptionFile description = plugin.getDescription();
        System.out.println(ChestShop.chatPrefix + description.getName() + " version " + description.getVersion() + " loaded.");
    }

    private static String generateOutdatedVersion(String pluginName, String curVersion, String neededVersion) {
        return ChestShop.chatPrefix + "Your " + pluginName + " is outdated! Need version AT LEAST " + neededVersion + " - Your version is " + curVersion;
    }
}
