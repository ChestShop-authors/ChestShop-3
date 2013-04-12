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
    private static EconomyManager manager = new EconomyManager();

    public static boolean transactionCanFail() {
        return manager.transactionCanFail();
    }

    public static boolean isOwnerEconomicallyActive(Inventory inventory) {
        return !ChestShopSign.isAdminShop(inventory) || !getServerAccountName().isEmpty();
    }

    public static boolean hasAccount(String player) {
        return !player.isEmpty() && manager.hasAccount(uName.getName(player));
    }

    public static String getServerAccountName() {
        return Properties.SERVER_ECONOMY_ACCOUNT;
    }

    public static boolean isServerAccount(String acc) {
        return ChestShopSign.isAdminShop(acc);
    }

    public static boolean isBank(String name) {
        return name.startsWith(uName.BANK_PREFIX);
    }

    public static boolean hasBankSupport() {
        return manager.hasBankSupport();
    }

    public static boolean bankExists(String name) {
        return manager.bankExists(name);
    }

    public static boolean add(String name, double amount) {
        if (isServerAccount(name)) {
            if (!getServerAccountName().isEmpty()) {
                name = getServerAccountName();
            } else {
                return true;
            }
        }

        float taxAmount = isServerAccount(name) ? Properties.SERVER_TAX_AMOUNT : isBank(name) ? Properties.BANK_TAX_AMOUNT : Properties.TAX_AMOUNT;

        double tax = getTax(taxAmount, amount);
        if (tax != 0) {
            if (!getServerAccountName().isEmpty()) {
                manager.add(getServerAccountName(), tax);
            }
            amount -= tax;
        }

        name = uName.getName(name);
        amount = roundUp(amount);
        if (isBank(name)) {
            if (hasBankSupport()) {
                return manager.bankAdd(uName.stripBankPrefix(name), amount);
            } else {
                return false;
            }
        } else {
            return manager.add(name, amount);
        }
    }

    public static double getTax(float tax, double price) {
        return NumberUtil.roundDown((tax / 100F) * price);
    }

    public static boolean subtract(String name, double amount) {
        if (isServerAccount(name)) {
            if (!getServerAccountName().isEmpty()) {
                name = getServerAccountName();
            } else {
                return true;
            }
        }

        name = uName.getName(name);
        amount = roundUp(amount);
        if (isBank(name)) {
            if (hasBankSupport()) {
                return manager.bankSubtract(uName.stripBankPrefix(name), amount);
            } else {
                return false;
            }
        } else {
            return manager.subtract(name, amount);
        }
    }

    public static boolean canHold(String name, double amount) {
        if (!transactionCanFail()) {
            return true;
        }

        if (isServerAccount(name)) {
            if (!getServerAccountName().isEmpty()) {
                name = getServerAccountName();
            } else {
                return true;
            }
        }

        name = uName.getName(name);
        amount = roundUp(amount);
        if (isBank(name)) {
            if (hasBankSupport()) {
                if (!manager.bankAdd(name, amount)) {
                    return false;
                } else {
                    manager.bankSubtract(name, amount);
                }
            } else {
                return false;
            }
        } else {
            if (!manager.add(name, amount)) {
                return false;
            } else {
                manager.subtract(name, amount);
            }
        }

        return true;
    }

    public static boolean hasEnough(String name, double amount) {
        if (amount <= 0) {
            return true;
        }

        if (isServerAccount(name)) {
            if (!getServerAccountName().isEmpty()) {
                name = getServerAccountName();
            } else {
                return true;
            }
        }

        name = uName.getName(name);
        amount = roundUp(amount);
        if (isBank(name)) {
            if (hasBankSupport()) {
                return manager.bankHasEnough(uName.stripBankPrefix(name), amount);
            } else {
                return false;
            }
        } else {
            return manager.hasEnough(name, amount);
        }
    }

    public static double getBalance(String name) {
        if (isServerAccount(name)) {
            if (!getServerAccountName().isEmpty()) {
                name = getServerAccountName();
            } else {
                return Double.MAX_VALUE;
            }
        }

        name = uName.getName(name);
        if (isBank(name)) {
            if (hasBankSupport()) {
                return manager.bankBalance(uName.stripBankPrefix(name));
            } else {
                return 0;
            }
        } else {
            return manager.balance(name);
        }
    }

    public static String formatBalance(double amount) {
        return manager.format(roundUp(amount));
    }

    public static boolean isBankOwner(String player, String bank) {
        if (hasBankSupport()) {
            return manager.isBankOwner(player, bank);
        } else {
            return false;
        }
    }

    public static boolean isBankMember(String player, String bank) {
        if (hasBankSupport()) {
            return manager.isBankMember(player, bank);
        } else {
            return false;
        }
    }

    public static void setPlugin(EconomyManager plugin) {
        manager = plugin;
    }

    public static EconomyManager getManager() {
        return manager;
    }

    public static boolean isLoaded() {
        return manager.getClass() != EconomyManager.class;
    }
}
