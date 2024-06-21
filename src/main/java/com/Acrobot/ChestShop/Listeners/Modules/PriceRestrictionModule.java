package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ChestShopReloadEvent;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.logging.Level;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.*;
import static com.Acrobot.ChestShop.Permission.*;

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

        configuration.options().header("In this file you can configure maximum and minimum prices for items (when creating a shop).\nBy default, entries are read as Materials. If you wish to use other items, simply enter the name as written on the /iteminfo command.\nExact matches to the /iteminfo name are prioritised, and materials are a fallback.");

        if (!file.exists()) {
            configuration.addDefault("uses_materials", true);

            configuration.addDefault("max.buy_price.item_type", 5.53);
            configuration.addDefault("max.buy_price.piston_head", 3.51);
            configuration.addDefault("max.sell_price.placed_banner", 3.52);

            configuration.addDefault("min.buy_price.piston_head", 1.03);
            configuration.addDefault("min.sell_price.placed_banner", 0.51);

            // Add example of custom item to the config
            configuration.addDefault("max.buy_price.Powered Rail#1", 3.51);
            configuration.addDefault("max.sell_price.Powered Rail#1", 3.52);


            try {
                configuration.options().copyDefaults(true);
                configuration.save(ChestShop.loadFile("priceLimits.yml"));
            } catch (IOException e) {
                ChestShop.getBukkitLogger().log(Level.SEVERE, "Error while loading priceLimits.yml", e);
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
                    ChestShop.getBukkitLogger().log(Level.SEVERE, "Error while converting priceLimits.yml", e);
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

    /**
     * Evaluate whether the configPath leads to a item or not
     * 
     * @param configPathToItem the config path
     * @return true if contained in config, false otherwise
     */
    private boolean isValid(String configPathToItem) {
        return configuration.getDouble(configPathToItem, INVALID_PATH) != INVALID_PATH;
    }

    /**
     * Get the item reference for an item: First try get path via itemStack's getSignName, if valid
     * return the getSignName, otherwise return path using itemStack's Material
     * 
     * @param maxMinPath The min/max path
     * @param itemStack The itemstack to get the config path for
     * @return the getSignName for the itemStack, or the item's material
     */
    private String getItemReference(String maxMinPath, ItemStack itemStack) {
        String signName = ItemUtil.getSignName(itemStack);
        // If there is a valid path to the itemstack using getSignName, return signName
        // otherwise return the item material
        return isValid((maxMinPath + signName)) ? signName
                : itemStack.getType().toString().toLowerCase(Locale.ROOT);
    }

    /**
     * Get the config path for the item 1: First try get path via itemStack's getSignName, if valid
     * return the getSignName, otherwise return path using itemStack's Material
     * 
     * @param maxMinPath The min/max path
     * @param itemStack The itemstack to get the config path for
     * @return the config path to the itemstack
     */
    private String getConfigPath(String maxMinPath, ItemStack itemStack) {
        return maxMinPath + getItemReference(maxMinPath, itemStack);
    }

    private BigDecimal getLimit(String itemConfigPath, int amount) {
        return BigDecimal.valueOf(configuration.getDouble(itemConfigPath) * amount);
    }


    @EventHandler
    public void onPreShopCreation(PreShopCreationEvent event) {
        ItemParseEvent parseEvent = new ItemParseEvent(ChestShopSign.getItem(event.getSignLines()));
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack itemStack = parseEvent.getItem();
        Player player = event.getPlayer();

        if (itemStack == null) {
            return;
        }

        int amount;
        try {
            amount = ChestShopSign.getQuantity(event.getSignLines());
        } catch (IllegalArgumentException e) {
            return;
        }

        String priceLine = ChestShopSign.getPrice(event.getSignLines());
        if (PriceUtil.hasBuyPrice(priceLine)) {
            BigDecimal buyPrice = PriceUtil.getExactBuyPrice(priceLine);

            String minBuyItemPath = getConfigPath("min.buy_price.", itemStack);
            BigDecimal minBuyPrice = getLimit(minBuyItemPath, amount);
            if (isValid(minBuyItemPath) && buyPrice.compareTo(minBuyPrice) < 0
                    && !Permission.has(player, NOLIMIT_MIN_BUY) && !Permission.has(player, NOLIMIT_MIN_BUY_ID + getItemReference("min.buy_price.", itemStack))) {
                event.setOutcome(BUY_PRICE_BELOW_MIN);
                Messages.BUY_PRICE_BELOW_MIN.sendWithPrefix(player, "price", buyPrice.toPlainString(), "minprice", minBuyPrice.toPlainString());
            }

            String maxBuyItemPath = getConfigPath("max.buy_price.", itemStack);
            BigDecimal maxBuyPrice = getLimit(maxBuyItemPath, amount);
            if (isValid(maxBuyItemPath) && buyPrice.compareTo(maxBuyPrice) > 0
                    && !Permission.has(player, NOLIMIT_MAX_BUY) && !Permission.has(player, NOLIMIT_MAX_BUY_ID + getItemReference("max.buy_price.", itemStack))) {
                event.setOutcome(BUY_PRICE_ABOVE_MAX);
                Messages.BUY_PRICE_ABOVE_MAX.sendWithPrefix(player, "price", buyPrice.toPlainString(), "maxprice", maxBuyPrice.toPlainString());
            }
        }

        if (PriceUtil.hasSellPrice(priceLine)) {
            BigDecimal sellPrice = PriceUtil.getExactSellPrice(priceLine);

            String minSellItemPath = getConfigPath("min.sell_price.", itemStack);
            BigDecimal minSellPrice = getLimit(minSellItemPath, amount);
            if (isValid(minSellItemPath) && sellPrice.compareTo(minSellPrice) < 0
                    && !Permission.has(player, NOLIMIT_MIN_SELL) && !Permission.has(player, NOLIMIT_MIN_SELL_ID + getItemReference("min.sell_price.", itemStack))) {
                event.setOutcome(SELL_PRICE_BELOW_MIN);
                Messages.SELL_PRICE_BELOW_MIN.sendWithPrefix(player, "price", sellPrice.toPlainString(),  "minprice", minSellPrice.toPlainString());
            }

            String maxSellItemPath = getConfigPath("max.sell_price.", itemStack);
            BigDecimal maxSellPrice = getLimit(maxSellItemPath, amount);
            if (isValid(maxSellItemPath) && sellPrice.compareTo(maxSellPrice) > 0
                    && !Permission.has(player, NOLIMIT_MAX_SELL) && !Permission.has(player, NOLIMIT_MAX_SELL_ID + getItemReference("max.sell_price.", itemStack))) {
                event.setOutcome(SELL_PRICE_ABOVE_MAX);
                Messages.SELL_PRICE_ABOVE_MAX.sendWithPrefix(player, "price", sellPrice.toPlainString(),  "maxprice", maxSellPrice.toPlainString());
            }
        }
    }
}
