package com.Acrobot.ChestShop.Economy;

import com.nijikokun.register.payment.forChestShop.Method;

/**
 * @author Acrobot
 */
public class Register extends EconomyManager {
    public Method method;

    public Register(Method method) {
        this.method = method;
    }

    public boolean hasAccount(String player) {
        return method.hasAccount(player);
    }

    public boolean add(String player, double amount) {
        return method.getAccount(player).add(amount);
    }

    public boolean subtract(String player, double amount) {
        return method.getAccount(player).subtract(amount);
    }

    public boolean hasEnough(String player, double amount) {
        return method.getAccount(player).hasEnough(amount);
    }

    public double balance(String player) {
        return method.getAccount(player).balance();
    }

    public String format(double amount) {
        return method.format(amount);
    }
}
