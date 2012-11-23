package com.Acrobot.ChestShop.Economy;

import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.inventory.Inventory;

import static com.Acrobot.Breeze.Utils.NumberUtil.roundUp;

/**
 * @author Acrobot
 *         Economy management
 */
public class Economy {
    private static EcoPlugin economy;

    public static boolean isOwnerEconomicallyActive(Inventory inventory) {
        return !ChestShopSign.isAdminShop(inventory) || !getServerAccountName().isEmpty();
    }

    public static boolean hasAccount(String p) {
        return !p.isEmpty() && economy.hasAccount(uName.getName(p));
    }

    public static String getServerAccountName() {
        return Properties.SERVER_ECONOMY_ACCOUNT;
    }

    public static boolean isServerAccount(String acc) {
        return ChestShopSign.isAdminShop(acc);
    }

    public static void add(String name, double amount) {
        if (isServerAccount(name) && !getServerAccountName().isEmpty()) {
            name = getServerAccountName();
        }

        float taxAmount = isServerAccount(name) ? Properties.SERVER_TAX_AMOUNT : Properties.TAX_AMOUNT;

        double tax = getTax(taxAmount, amount);
        if (tax != 0) {
            if (!getServerAccountName().isEmpty()) {
                economy.add(getServerAccountName(), tax);
            }
            amount -= tax;
        }

        economy.add(uName.getName(name), amount);
    }

    public static double getTax(float tax, double price) {
        return NumberUtil.roundDown((tax / 100F) * price);
    }

    public static void subtract(String name, double amount) {
        if (isServerAccount(name) && !getServerAccountName().isEmpty()) {
            name = getServerAccountName();
        }

        economy.subtract(uName.getName(name), roundUp(amount));
    }

    public static boolean hasEnough(String name, double amount) {
        if (amount <= 0) {
            return true;
        }
        if (isServerAccount(name) && !getServerAccountName().isEmpty()) {
            name = getServerAccountName();
        }

        return economy.hasEnough(uName.getName(name), roundUp(amount));
    }

    public static double getBalance(String name) {
        if (isServerAccount(name) && !getServerAccountName().isEmpty()) {
            name = getServerAccountName();
        }

        return economy.balance(uName.getName(name));
    }

    public static String formatBalance(double amount) {
        return economy.format(roundUp(amount));
    }

    public static void setPlugin(EcoPlugin plugin) {
        economy = plugin;
    }

    public static EcoPlugin getPlugin() {
        return economy;
    }

    public static boolean isLoaded() {
        return economy != null;
    }
}
