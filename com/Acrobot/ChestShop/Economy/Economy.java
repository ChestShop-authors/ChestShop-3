package com.Acrobot.ChestShop.Economy;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Utils.uName;

/**
 * @author Acrobot
 *         Economy management
 */
public class Economy {
    public static EcoPlugin economy;

    public static boolean hasAccount(String p) {
        return !p.isEmpty() && economy.hasAccount(uName.getName(p));
    }

    public static void add(String name, double amount) {
        if (!hasAccount(name)) {
            return;
        }

        String serverAccount = Config.getString(Property.SERVER_ECONOMY_ACCOUNT);
        Property taxAmount = name.equals(serverAccount) ? Property.SERVER_TAX_AMOUNT : Property.TAX_AMOUNT;

        if (Config.getFloat(taxAmount) != 0) {
            double tax = getTax(taxAmount, amount);
            if (!serverAccount.isEmpty()) {
                economy.add(serverAccount, tax);
            }
            amount -= tax;
        }

        economy.add(uName.getName(name), amount);
    }

    public static double getTax(Property tax, double price) {
        return (Config.getFloat(tax) / 100F) * price;
    }

    public static void subtract(String name, double amount) {
        if (!hasAccount(name)) {
            return;
        }

        economy.subtract(uName.getName(name), amount);
    }

    public static boolean hasEnough(String name, double amount) {
        if (!hasAccount(name)) {
            return true;
        }

        return economy.hasEnough(uName.getName(name), amount);
    }

    public static double balance(String name) {
        return economy.balance(uName.getName(name));
    }

    public static String formatBalance(double amount) {
        return economy.format(amount);
    }
}
