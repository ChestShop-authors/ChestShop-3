package com.Acrobot.ChestShop.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Acrobot
 */
public class Vault implements EcoPlugin {
    private static net.milkbowl.vault.economy.Economy economy;

    public boolean hasAccount(String player) {
        return economy.hasAccount(player);
    }

    public void add(String player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public void subtract(String player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public boolean hasEnough(String player, double amount) {
        return economy.has(player, amount);
    }

    public double balance(String player) {
        return economy.getBalance(player);
    }

    public String format(double amount) {
        return economy.format(amount);
    }

    public static String getPluginName() {
        if (economy == null) {
            return "";
        } else {
            return economy.getName();
        }
    }

    public static Vault getVault() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (rsp == null) {
            return null;
        }

        economy = rsp.getProvider();

        if (economy == null) {
            return null;
        } else {
            return new Vault();
        }
    }
}
