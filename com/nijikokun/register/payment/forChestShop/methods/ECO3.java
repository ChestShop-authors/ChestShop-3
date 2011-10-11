package com.nijikokun.register.payment.forChestShop.methods;

import com.nijikokun.register.payment.forChestShop.Method;
import me.ic3d.eco.ECO;
import org.bukkit.plugin.Plugin;

/**
 * 3co implementation of Method.
 *
 * @copyright (c) 2011
 * @license AOL license <http://aol.nexua.org>
 */
public class ECO3 implements Method {
    private ECO eco;

    public Object getPlugin() {
        return this.eco;
    }

    public String getName() {
        return "3co";
    }

    public String getVersion() {
        return "2.0";
    }

    public int fractionalDigits() {
        return 0;
    }

    public String format(double amount) {
        return (int) Math.ceil(amount) + " " + (amount == 1 ? eco.singularCurrency : eco.pluralCurrency);
    }

    public boolean hasBanks() {
        return false;
    }

    public boolean hasBank(String bank) {
        return false;
    }

    public boolean hasAccount(String name) {
        return eco.hasAccount(name);
    }

    public boolean hasBankAccount(String bank, String name) {
        return false;
    }

    public boolean createAccount(String name) {
        if (hasAccount(name)) return false;
        eco.createAccount(name, 0);
        return true;
    }

    public boolean createAccount(String name, double balance) {
        if (hasAccount(name)) return false;
        eco.createAccount(name, (int) balance);
        return true;
    }

    public MethodAccount getAccount(String name) {
        if (!hasAccount(name)) createAccount(name); //Still somehow fails - it's 3co's issue
        return new ECO3Account(name);
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        return null;
    }

    public boolean isCompatible(Plugin plugin) {
        return plugin.getDescription().getName().equals("3co") && plugin.getClass().getName().equals("me.ic3d.eco.ECO") && plugin instanceof ECO;
    }

    public void setPlugin(Plugin plugin) {
        this.eco = (ECO) plugin;
    }

    public class ECO3Account implements MethodAccount {
        private String name;

        public ECO3Account(String name) {
            this.name = name;
        }

        public double balance() {
            return eco.getMoney(name);
        }

        public boolean set(double amount) {
            eco.setMoney(name, (int) Math.ceil(amount));
            return true;
        }

        public boolean add(double amount) {
            set(balance() + amount);
            return true;
        }

        public boolean subtract(double amount) {
            set(balance() - amount);
            return true;
        }

        public boolean multiply(double amount) {
            set(balance() * amount);
            return true;
        }

        public boolean divide(double amount) {
            set(balance() / amount);
            return true;
        }

        public boolean hasEnough(double amount) {
            return eco.hasEnough(name, (int) Math.ceil(amount));
        }

        public boolean hasOver(double amount) {
            return balance() > amount;
        }

        public boolean hasUnder(double amount) {
            return balance() < amount;
        }

        public boolean isNegative() {
            return balance() < 0;
        }

        public boolean remove() {
            return false;
        }
    }
}
