package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Economy.NoProvider;
import com.Acrobot.ChestShop.Economy.Register;
import com.Acrobot.ChestShop.Economy.Vault;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Plugins.*;
import com.griefcraft.lwc.LWC;
import com.nijikokun.register.payment.forChestShop.Method;
import com.nijikokun.register.payment.forChestShop.Methods;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.webkonsept.bukkit.simplechestlock.SCL;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Acrobot
 */
public class Dependencies {

    public static void load() {
        initializeSecurity();

        for (Object plugin : ChestShop.getDependencies()) {
            Plugin pl = ChestShop.getPluginManager().getPlugin((String) plugin);
            if (pl != null) {
                initializePlugin((String) plugin, pl);
            }
        }
        loadRegister();
    }

    private static void initializeSecurity() {
        ChestShop.registerListener(new com.Acrobot.ChestShop.Plugins.ChestShop());
    }

    private static void loadRegister() {
        if (Economy.economy != null) {
            return;
        }

        Method method = Methods.load(ChestShop.getPluginManager());
        if (method == null) {
            Economy.economy = new NoProvider();
            return;
        }
        Economy.economy = new Register(method);
        ChestShop.getBukkitLogger().info(method.getName() + " version " + method.getVersion() + " loaded.");
    }

    private static void initializePlugin(String name, Plugin plugin) { //Really messy, right? But it's short and fast :)
        if (name.equals("LWC")) {
            ChestShop.registerListener(new LightweightChestProtection(LWC.getInstance()));
        } else if (name.equals("Lockette")) {
            ChestShop.registerListener(new Lockette());
        } else if (name.equals("Deadbolt")) {
            ChestShop.registerListener(new Deadbolt());
        } else if (name.equals("OddItem")) {
            Odd.isInitialized = true;
        } else if (name.equals("Towny")) {
            if (!Config.getBoolean(Property.TOWNY_INTEGRATION)) {
                return;
            }
            ChestShop.registerListener(new Towny());
        } else if (name.equals("WorldGuard")) {
            WorldGuardPlugin worldGuard = (WorldGuardPlugin) plugin;
            if (Config.getBoolean(Property.WORLDGUARD_USE_PROTECTION)) {
                ChestShop.registerListener(new WorldGuardProtection(worldGuard));
            }
            if (!Config.getBoolean(Property.WORLDGUARD_INTEGRATION)) {
                return;
            }
            ChestShop.registerListener(new WorldGuardBuilding(worldGuard));
        } else if (name.equals("Vault")) {
            if (Economy.economy != null) return;

            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = ChestShop.getBukkitServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (rsp == null) return;

            Vault.economy = rsp.getProvider();
            if (Vault.economy == null) return;

            Economy.economy = new Vault();
            ChestShop.getBukkitLogger().info("Vault loaded - using " + Vault.economy.getName());
            return;
        } else if (name.equals("Heroes")) {
            ChestShop.registerListener(new Heroes((com.herocraftonline.heroes.Heroes) plugin));
        } else if (name.equals("SimpleChestLock")) {
            ChestShop.registerListener(new SimpleChestLock((SCL) plugin));
        } else {
            return;
        }

        PluginDescriptionFile description = plugin.getDescription();
        ChestShop.getBukkitLogger().info(description.getName() + " version " + description.getVersion() + " loaded.");
    }
}
