package com.Acrobot.ChestShop.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

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
    }

    public boolean subtract(String player, double amount) {
        return vaultPlugin.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean hasEnough(String player, double amount) {
        return vaultPlugin.has(player, amount);
    }

    public double balance(String player) {
        return vaultPlugin.getBalance(player);
    }

    public boolean hasBankSupport() {
        return vaultPlugin.hasBankSupport() && !getPluginName().startsWith("iConomy");
    }

    public boolean bankExists(String bank) {
        bank = bank.toLowerCase();
        List<String> banks = vaultPlugin.getBanks();
        for (String entry : banks) {
            if (bank.equals(entry.toLowerCase())) return true;
        }
        return false;
    }

    public boolean bankAdd(String bank, double amount) {
        return vaultPlugin.bankDeposit(bank, amount).transactionSuccess();
    }

    public boolean bankSubtract(String bank, double amount) {
        return vaultPlugin.bankWithdraw(bank, amount).transactionSuccess();
    }

    public boolean bankHasEnough(String bank, double amount) {
        return vaultPlugin.bankHas(bank, amount).transactionSuccess();
    }

    public double bankBalance(String bank) {
        return vaultPlugin.bankBalance(bank).amount;
    }

    public boolean isBankOwner(String player, String bank) {
        return vaultPlugin.isBankOwner(bank, player).transactionSuccess();
    }

    public boolean isBankMember(String player, String bank) {
        return vaultPlugin.isBankMember(bank, player).transactionSuccess();
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
