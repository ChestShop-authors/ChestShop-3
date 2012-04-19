package com.Acrobot.ChestShop.Config;

import org.bukkit.Material;

/**
 * @author Acrobot
 */
public class MaxPrice {
    public static boolean canCreate(double buyPrice, double sellPrice, Material mat) {
        return buyPriceWithinRange(buyPrice, mat) && sellPriceWithinRange(sellPrice, mat);
    }

    private static boolean buyPriceWithinRange(double buyPrice, Material material) {
        double bPrice = maxBuyPrice(material);
        double maxPrice = maxBuyPrice();

        return buyPrice <= bPrice && buyPrice <= maxPrice;
    }

    private static boolean sellPriceWithinRange(double sellPrice, Material material) {
        double sPrice = maxSellPrice(material);
        double maxPrice = maxSellPrice();

        return sellPrice <= sPrice && sellPrice <= maxPrice;
    }

    public static double maxBuyPrice() {
        return getPrice(Price.buy);
    }

    public static double maxSellPrice() {
        return getPrice(Price.sell);
    }

    public static double maxBuyPrice(Material material) {
        return getPrice(Price.buy, material.getId());
    }

    public static double maxSellPrice(Material material) {
        return getPrice(Price.sell, material.getId());
    }

    public static double getPrice(Price price) {
        return getPrice(price, -1);
    }

    public static double getPrice(Price price, int itemID) {
        String node = "max-" + price + "-price" + (itemID > 0 ? "-" + itemID : "");
        return Config.exists(node) ? Config.getDouble(node) : Double.MAX_VALUE;
    }

    private static enum Price {
        buy,
        sell
    }
}
