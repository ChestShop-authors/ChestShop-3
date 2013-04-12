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

    public boolean transactionCanFail() {
        return false;
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

    public boolean hasBankSupport() {
        return false;
    }

    public boolean bankExists(String bank) {
        return false;
    }

    public boolean bankAdd(String bank, double amount) {
        return false;
    }

    public boolean bankSubtract(String bank, double amount) {
        return false;
    }

    public boolean bankHasEnough(String bank, double amount) {
        return false;
    }

    public double bankBalance(String bank) {
        return 0;
    }

    public boolean isBankOwner(String player, String bank) {
        return false;
    }

    public boolean isBankMember(String player, String bank) {
        return false;
    }

    public String format(double amount) {
        return method.format(amount);
    }
}
