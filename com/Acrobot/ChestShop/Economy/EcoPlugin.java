package com.Acrobot.ChestShop.Economy;

/**
 * @author Acrobot
 */
public interface EcoPlugin {
    boolean hasAccount(String player);

    void add(String player, double amount);

    void subtract(String player, double amount);

    boolean hasEnough(String player, double amount);

    double balance(String player);

    String format(double amount);
}
