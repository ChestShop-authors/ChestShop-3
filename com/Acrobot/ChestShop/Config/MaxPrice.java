package com.Acrobot.ChestShop.Config;

import org.bukkit.Material;

/**
 * @author Acrobot
 */
public class MaxPrice {
    public static boolean canCreate(float buyPrice, float sellPrice, Material mat) {
        float bPrice = maxBuyPrice(mat.getId());
        float sPrice = maxSellPrice(mat.getId());
        
        return (bPrice == -1 || buyPrice <= maxBuyPrice(mat.getId()))
                && (sPrice == -1 || sellPrice <= maxSellPrice(mat.getId()));
    }
    
    public static float maxBuyPrice(int itemID) {
        return getPrice("buy", itemID);
    }

    public static float maxSellPrice(int itemID) {
        return getPrice("sell", itemID);
    }

    public static float getPrice(String value, int itemID) {
        String node = "max-" + value + "-price-" + itemID;
        return Config.exists(node) ? Config.getFloat(node) : -1;
    }
}
