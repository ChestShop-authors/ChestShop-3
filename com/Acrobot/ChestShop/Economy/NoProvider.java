package com.Acrobot.ChestShop.Economy;

/**
 * @author Acrobot
 */
public class NoProvider implements EcoPlugin{
    public boolean hasAccount(String player) {
        System.out.println("[ChestShop] You haven't got any economy plugin!");
        return false;
    }

    public void add(String player, double amount) {
        System.out.println("[ChestShop] You haven't got any economy plugin!");
    }

    public void subtract(String player, double amount) {
        System.out.println("[ChestShop] You haven't got any economy plugin!");
    }

    public boolean hasEnough(String player, double amount) {
        System.out.println("[ChestShop] You haven't got any economy plugin!");
        return false;
    }

    public double balance(String player) {
        System.out.println("[ChestShop] You haven't got any economy plugin!");
        return 0;
    }

    public String format(double amount) {
        System.out.println("[ChestShop] You haven't got any economy plugin!");
        return null;
    }
}
