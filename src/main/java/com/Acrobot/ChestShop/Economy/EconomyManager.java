package com.Acrobot.ChestShop.Economy;

import com.Acrobot.ChestShop.ChestShop;

/**
 * @author Acrobot
 */
public class EconomyManager {
    public boolean transactionCanFail() {
        return false;
    }

    public boolean hasAccount(String player) {
        printError();
        return false;
    }

    public boolean add(String player, double amount) {
        printError();
        return false;
    }

    public boolean subtract(String player, double amount) {
        printError();
        return false;
    }

    public boolean hasEnough(String player, double amount) {
        printError();
        return false;
    }

    public double balance(String player) {
        printError();
        return 0;
    }

    public boolean hasBankSupport() {
        printError();
        return false;
    }

    public boolean bankExists(String bank) {
        printError();
        return false;
    }

    public boolean bankAdd(String bank, double amount) {
        printError();
        return false;
    }

    public boolean bankSubtract(String bank, double amount) {
        printError();
        return false;
    }

    public boolean bankHasEnough(String bank, double amount) {
        printError();
        return false;
    }

    public double bankBalance(String bank) {
        printError();
        return 0;
    }

    public boolean isBankOwner(String player, String bank) {
        printError();
        return false;
    }

    public boolean isBankMember(String player, String bank) {
        printError();
        return false;
    }

    public String format(double amount) {
        printError();
        return null;
    }

    private static void printError() {
        ChestShop.getBukkitLogger().severe("You haven't got any economy plugin or your economical plugin is not supported by the build-in system!");
        ChestShop.getBukkitLogger().severe("Please download an economy plugin or, if your plugin is not recognised, download Vault.");
    }
}
