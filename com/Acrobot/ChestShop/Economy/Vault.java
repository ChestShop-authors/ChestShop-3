package com.Acrobot.ChestShop.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Acrobot
 */
public class Vault extends EconomyManager {
    private static net.milkbowl.vault.economy.Economy vaultPlugin;

    public boolean hasAccount(String player) {
        return vaultPlugin.hasAccount(player);
    }

    public void add(String player, double amount) {
        vaultPlugin.depositPlayer(player, amount);
    }

    public void subtract(String player, double amount) {
        vaultPlugin.withdrawPlayer(player, amount);
    }

    public boolean hasEnough(String player, double amount) {
        return vaultPlugin.has(player, amount);
    }

    public double balance(String player) {
        return vaultPlugin.getBalance(player);
    }

    public String format(double amount) {
        return vaultPlugin.format(amount);
    }

    public static String getPluginName() {
        if (vaultPlugin == null) {
            return "";
        } else {
            return vaultPlugin.getName();
        }
    }

    public static Vault getVault() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (rsp == null) {
            return null;
        }

        vaultPlugin = rsp.getProvider();

        if (vaultPlugin == null) {
            return null;
        } else {
            return new Vault();
        }
    }
}
