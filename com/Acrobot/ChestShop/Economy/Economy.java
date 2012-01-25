package com.Acrobot.ChestShop.Economy;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Utils.uLongName;

/**
 * @author Acrobot
 *         Economy management
 */
public class Economy {
    public static EcoPlugin economy;

    public static boolean hasAccount(String p) {
        return economy.hasAccount(uLongName.getName(p));
    }

    public static void add(String name, float amount) {
        String account = Config.getString(Property.SERVER_ECONOMY_ACCOUNT);
        if (!account.isEmpty()) {
            float tax = getTax(Property.TAX_AMOUNT, amount);
            economy.add(account, tax);
            amount = amount - tax;
        }

        economy.add(uLongName.getName(name), amount);
    }

    public static void addServer(String name, float amount){
        String account = Config.getString(Property.SERVER_ECONOMY_ACCOUNT);
        if (!account.isEmpty()) {
            float tax = getTax(Property.SERVER_TAX_AMOUNT, amount);
            economy.add(account, tax);
            amount = amount - tax;
        }
        economy.add(uLongName.getName(name), amount);
    }

    public static float getTax(Property tax, float price){
        return (Config.getFloat(tax) / 100F) * price;
    }

    public static void subtract(String name, float amount) {
        economy.subtract(name, amount);
    }

    public static boolean hasEnough(String name, float amount) {
        return economy.hasEnough(uLongName.getName(name), amount);
    }

    public static double balance(String name) {
        return economy.balance(uLongName.getName(name));
    }

    public static String formatBalance(double amount) {
        return economy.format(amount);
    }
}
