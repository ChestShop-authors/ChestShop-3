package com.Acrobot.ChestShop.Economy;

import com.Acrobot.ChestShop.ChestShop;

/**
 * @author Acrobot
 */
public class EconomyManager {
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

    public String format(double amount) {
        printError();
        return null;
    }

    private static void printError() {
        ChestShop.getBukkitLogger().severe("You haven't got any economy plugin or your economical plugin is not supported by the build-in system!");
        ChestShop.getBukkitLogger().severe("Please download an economy plugin or, if your plugin is not recognised, download Vault.");
    }
}
