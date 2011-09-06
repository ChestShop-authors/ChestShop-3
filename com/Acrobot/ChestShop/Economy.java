package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Utils.uLongName;
import com.LRFLEW.register.payment.forChestShop.Method;

/**
 * @author Acrobot
 *         Economy management
 */
public class Economy {
    public static Method economy;

    public static boolean hasAccount(String p) {
        return economy.hasAccount(uLongName.getName(p));
    }

    public static void add(String name, float amount) {
        economy.getAccount(uLongName.getName(name)).add(amount);
    }

    public static void substract(String name, float amount) {
        economy.getAccount(uLongName.getName(name)).subtract(amount);
    }

    public static boolean hasEnough(String name, float amount) {
        return economy.getAccount(uLongName.getName(name)).hasEnough(amount);
    }

    public static double balance(String name) {
        return economy.getAccount(uLongName.getName(name)).balance();
    }

    public static String formatBalance(double amount) {
        return economy.format(amount);
    }
}
