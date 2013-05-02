package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_PRICE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;

/**
 * @author Acrobot
 */
public class PriceRestrictionModule implements Listener {
    private YamlConfiguration configuration;
    private static final double INVALID_PATH = Double.MIN_VALUE;

    public PriceRestrictionModule() {
        File file = new File(ChestShop.getFolder(), "priceLimits.yml");

        configuration = YamlConfiguration.loadConfiguration(file);

        configuration.options().header("In this file you can configure maximum and minimum prices for items (when creating a shop).");

        if (!file.exists()) {
            configuration.addDefault("max.buy_price.itemID", 5.53);
            configuration.addDefault("max.buy_price.988", 3.51);
            configuration.addDefault("max.sell_price.978", 3.52);

            configuration.addDefault("min.buy_price.979", 1.03);
            configuration.addDefault("min.sell_price.989", 0.51);

            try {
                configuration.options().copyDefaults(true);
                configuration.save(ChestShop.loadFile("priceLimits.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPreShopCreation(PreShopCreationEvent event) {
        ItemStack material = MaterialUtil.getItem(event.getSignLine(ITEM_LINE));

        if (material == null) {
            return;
        }

        int itemID = material.getTypeId();
        int amount = material.getAmount();

        if (PriceUtil.hasBuyPrice(event.getSignLine(PRICE_LINE))) {
            double buyPrice = PriceUtil.getBuyPrice(event.getSignLine(PRICE_LINE));

            if (isValid("min.buy_price." + itemID) && buyPrice < (configuration.getDouble("min.buy_price." + itemID) / amount)) {
                event.setOutcome(INVALID_PRICE);
            }

            if (isValid("max.buy_price." + itemID) && buyPrice > (configuration.getDouble("max.buy_price." + itemID) / amount)) {
                event.setOutcome(INVALID_PRICE);
            }
        }

        if (PriceUtil.hasSellPrice(event.getSignLine(PRICE_LINE))) {
            double sellPrice = PriceUtil.getSellPrice(event.getSignLine(PRICE_LINE));

            if (isValid("min.sell_price." + itemID) && sellPrice < (configuration.getDouble("min.sell_price." + itemID) / amount)) {
                event.setOutcome(INVALID_PRICE);
            }

            if (isValid("max.sell_price." + itemID) && sellPrice > (configuration.getDouble("max.sell_price." + itemID) / amount)) {
                event.setOutcome(INVALID_PRICE);
            }
        }
    }

    private boolean isValid(String path) {
        return configuration.getDouble(path, INVALID_PATH) != INVALID_PATH;
    }
}
