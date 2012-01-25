package com.Acrobot.ChestShop.Economy;

/**
 * @author Acrobot
 */
public class Vault implements EcoPlugin {
    public static net.milkbowl.vault.economy.Economy economy;

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
}
