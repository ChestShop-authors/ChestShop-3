package com.Acrobot.ChestShop.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.mciseries.iLogTransactions.TransactionEntry;

/**
 * @author Acrobot
 */
public class Vault extends EconomyManager {
    private static net.milkbowl.vault.economy.Economy vaultPlugin;

    public boolean transactionCanFail() {
        return getPluginName().equals("Gringotts") || getPluginName().equals("GoldIsMoney") || getPluginName().equals("MultiCurrency");
    }

    public boolean hasAccount(String player) {
        return vaultPlugin.hasAccount(player);
    }

    public boolean add(String player, double amount) {
        return vaultPlugin.depositPlayer(player, amount).transactionSuccess();
        if(!Bukkit.getPluginManager().getPlugin("iLogTransactions") == null) {
            new TransactionEntry("ChestShop", amount, true, player, "Got money from shop"); 
        }
    }

    public boolean subtract(String player, double amount) {
        return vaultPlugin.withdrawPlayer(player, amount).transactionSuccess();
        if(!Bukkit.getPluginManager().getPlugin("iLogTransactions") == null) {
            new TransactionEntry("ChestShop", amount, false, player, "Bought item from shop."); 
        }
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
