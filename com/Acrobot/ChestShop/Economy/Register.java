package com.Acrobot.ChestShop.Economy;

import com.nijikokun.register.payment.forChestShop.Method;

/**
 * @author Acrobot
 */
public class Register implements EcoPlugin {
    public Method eco;

    public Register(Method eco) {
        this.eco = eco;
    }

    public boolean hasAccount(String player) {
        return eco.hasAccount(player);
    }

    public void add(String player, double amount) {
        eco.getAccount(player).add(amount);
    }

    public void subtract(String player, double amount) {
        eco.getAccount(player).subtract(amount);
    }

    public boolean hasEnough(String player, double amount) {
        return eco.getAccount(player).hasEnough(amount);
    }

    public double balance(String player) {
        return eco.getAccount(player).balance();
    }

    public String format(double amount) {
        return eco.format(amount);
    }
}
