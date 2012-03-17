package com.Acrobot.ChestShop.Economy;

/**
 * @author Acrobot
 */
public interface EcoPlugin {
    public boolean hasAccount(String player);

    public void add(String player, double amount);

    public void subtract(String player, double amount);

    public boolean hasEnough(String player, double amount);

    public double balance(String player);

    public String format(double amount);
}
