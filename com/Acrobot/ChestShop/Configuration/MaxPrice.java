package com.Acrobot.ChestShop.Configuration;

import com.Acrobot.ChestShop.ChestShop;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Acrobot
 */
public class MaxPrice {
    private static Configuration config = YamlConfiguration.loadConfiguration(ChestShop.loadFile("config.yml"));

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
        return config.isSet(node) ? config.getDouble(node) : Double.MAX_VALUE;
    }

    private static enum Price {
        buy,
        sell
    }
}
