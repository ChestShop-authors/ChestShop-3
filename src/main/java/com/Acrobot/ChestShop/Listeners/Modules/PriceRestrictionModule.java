package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ChestShopReloadEvent;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
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

/**
 * @author Acrobot
 */
public class PriceRestrictionModule implements Listener {
    private YamlConfiguration configuration;
    private static final double INVALID_PATH = Double.MIN_VALUE;

    public PriceRestrictionModule() {
        load();
    }

    private void load() {
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
    public void onReload(ChestShopReloadEvent event) {
        load();
    }

    @EventHandler
    public void onPreShopCreation(PreShopCreationEvent event) {
        ItemParseEvent parseEvent = new ItemParseEvent(ChestShopSign.getItem(event.getSignLines()));
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack material = parseEvent.getItem();

        if (material == null) {
            return;
        }

        String itemType = material.getType().toString().toLowerCase(Locale.ROOT);
        int amount;
        try {
            amount = ChestShopSign.getQuantity(event.getSignLines());
        } catch (IllegalArgumentException e) {
            return;
        }

        String priceLine = ChestShopSign.getPrice(event.getSignLines());
        if (PriceUtil.hasBuyPrice(priceLine)) {
            BigDecimal buyPrice = PriceUtil.getExactBuyPrice(priceLine);

            BigDecimal minBuyPrice = BigDecimal.valueOf(configuration.getDouble("min.buy_price." + itemType) * amount);
            if (isValid("min.buy_price." + itemType) && buyPrice.compareTo(minBuyPrice) < 0) {
                event.setOutcome(BUY_PRICE_BELOW_MIN);
                Messages.BUY_PRICE_BELOW_MIN.sendWithPrefix(event.getPlayer(), "price", buyPrice.toPlainString(), "minprice", minBuyPrice.toPlainString());
            }

            BigDecimal maxBuyPrice = BigDecimal.valueOf(configuration.getDouble("max.buy_price." + itemType) * amount);
            if (isValid("max.buy_price." + itemType) && buyPrice.compareTo(maxBuyPrice) > 0) {
                event.setOutcome(BUY_PRICE_ABOVE_MAX);
                Messages.BUY_PRICE_ABOVE_MAX.sendWithPrefix(event.getPlayer(), "price", buyPrice.toPlainString(),  "maxprice", maxBuyPrice.toPlainString());
            }
        }

        if (PriceUtil.hasSellPrice(priceLine)) {
            BigDecimal sellPrice = PriceUtil.getExactSellPrice(priceLine);

            BigDecimal minSellPrice = BigDecimal.valueOf(configuration.getDouble("min.sell_price." + itemType) * amount);
            if (isValid("min.sell_price." + itemType) && sellPrice.compareTo(minSellPrice) < 0) {
                event.setOutcome(SELL_PRICE_BELOW_MIN);
                Messages.SELL_PRICE_BELOW_MIN.sendWithPrefix(event.getPlayer(), "price", sellPrice.toPlainString(),  "minprice", minSellPrice.toPlainString());
            }

            BigDecimal maxSellPrice = BigDecimal.valueOf(configuration.getDouble("max.sell_price." + itemType) * amount);
            if (isValid("max.sell_price." + itemType) && sellPrice.compareTo(maxSellPrice) > 0) {
                event.setOutcome(SELL_PRICE_ABOVE_MAX);
                Messages.SELL_PRICE_ABOVE_MAX.sendWithPrefix(event.getPlayer(), "price", sellPrice.toPlainString(),  "maxprice", maxSellPrice.toPlainString());
            }
        }
    }

    private boolean isValid(String path) {
        return configuration.getDouble(path, INVALID_PATH) != INVALID_PATH;
    }
}
