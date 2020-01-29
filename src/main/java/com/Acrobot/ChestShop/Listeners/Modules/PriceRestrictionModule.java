package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.logging.Level;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.BUY_PRICE_ABOVE_MAX;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.BUY_PRICE_BELOW_MIN;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.SELL_PRICE_ABOVE_MAX;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.SELL_PRICE_BELOW_MIN;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;

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
            configuration.addDefault("uses_materials", true);

            configuration.addDefault("max.buy_price.item_type", 5.53);
            configuration.addDefault("max.buy_price.piston_head", 3.51);
            configuration.addDefault("max.sell_price.placed_banner", 3.52);

            configuration.addDefault("min.buy_price.piston_head", 1.03);
            configuration.addDefault("min.sell_price.placed_banner", 0.51);

            try {
                configuration.options().copyDefaults(true);
                configuration.save(ChestShop.loadFile("priceLimits.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!configuration.getBoolean("uses_materials")) {
            Material testMat = Material.matchMaterial("1");
            if (testMat != null) {
                ChestShop.getBukkitLogger().log(Level.INFO, "Converting numeric IDs in priceLimits.yml to Material names...");
                convertToMaterial("max.buy_price");
                convertToMaterial("max.sell_price");
                convertToMaterial("min.buy_price");
                convertToMaterial("min.sell_price");
                configuration.set("uses_materials", true);
                try {
                    configuration.save(file);
                    ChestShop.getBukkitLogger().log(Level.INFO, "Conversion finished!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ChestShop.getBukkitLogger().log(Level.WARNING, "Could not convert numeric IDs in priceLimits.yml to Material names!");
                ChestShop.getBukkitLogger().log(Level.WARNING, "If you want to automatically convert them you have to run this version on a pre 1.13 server.");
                ChestShop.getBukkitLogger().log(Level.WARNING, "If you want to manually convert it and hide this message set the uses_materials key to true.");
            }
        }
    }

    private void convertToMaterial(String sectionPath) {
        ConfigurationSection section = configuration.getConfigurationSection(sectionPath);
        if (section != null) {
            for (String typeId : section.getKeys(false)) {
                Material material = Material.matchMaterial(typeId);
                if (material != null) {
                    configuration.set(sectionPath + "." + material.toString().toLowerCase(Locale.ROOT), configuration.get(sectionPath + "." + typeId));
                    configuration.set(sectionPath + "." + typeId, null);
                }
            }
        }
    }

    @EventHandler
    public void onPreShopCreation(PreShopCreationEvent event) {
        ItemParseEvent parseEvent = new ItemParseEvent(event.getSignLine(ITEM_LINE));
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack material = parseEvent.getItem();

        if (material == null) {
            return;
        }

        String itemType = material.getType().toString().toLowerCase(Locale.ROOT);
        int amount;
        try {
            amount = Integer.parseInt(event.getSignLine(QUANTITY_LINE));
        } catch (IllegalArgumentException e) {
            return;
        }

        if (PriceUtil.hasBuyPrice(event.getSignLine(PRICE_LINE))) {
            BigDecimal buyPrice = PriceUtil.getExactBuyPrice(event.getSignLine(PRICE_LINE));

            if (isValid("min.buy_price." + itemType) && buyPrice.compareTo(BigDecimal.valueOf(configuration.getDouble("min.buy_price." + itemType) * amount)) < 0) {
                event.setOutcome(BUY_PRICE_BELOW_MIN);
            }

            if (isValid("max.buy_price." + itemType) && buyPrice.compareTo(BigDecimal.valueOf(configuration.getDouble("max.buy_price." + itemType) * amount)) > 0) {
                event.setOutcome(BUY_PRICE_ABOVE_MAX);
            }
        }

        if (PriceUtil.hasSellPrice(event.getSignLine(PRICE_LINE))) {
            BigDecimal sellPrice = PriceUtil.getExactSellPrice(event.getSignLine(PRICE_LINE));

            if (isValid("min.sell_price." + itemType) && sellPrice.compareTo(BigDecimal.valueOf(configuration.getDouble("min.sell_price." + itemType) * amount)) < 0) {
                event.setOutcome(SELL_PRICE_BELOW_MIN);
            }

            if (isValid("max.sell_price." + itemType) && sellPrice.compareTo(BigDecimal.valueOf(configuration.getDouble("max.sell_price." + itemType) * amount)) > 0) {
                event.setOutcome(SELL_PRICE_ABOVE_MAX);
            }
        }
    }

    private boolean isValid(String path) {
        return configuration.getDouble(path, INVALID_PATH) != INVALID_PATH;
    }
}
