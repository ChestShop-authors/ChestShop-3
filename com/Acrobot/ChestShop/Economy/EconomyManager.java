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

    public void add(String player, double amount) {
        printError();
    }

    public void subtract(String player, double amount) {
        printError();
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
        ChestShop.getBukkitLogger().severe("You haven't got any economy plugin!");
    }
}
