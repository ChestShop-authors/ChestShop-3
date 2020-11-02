package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.IllegalFormatException;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;


/**
 * @author bricefrisco
 */
public class StockCounterModule implements Listener {
    private static final String PRICE_LINE_WITH_COUNT = "Q %d : C %d";

    @EventHandler(priority = EventPriority.HIGH)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        int quantity;
        try {
            quantity = QuantityUtil.parseQuantity(event.getSignLine(QUANTITY_LINE));
        } catch (IllegalArgumentException invalidQuantity) {
            return;
        }

        if (QuantityUtil.quantityLineContainsCounter(event.getSignLine(QUANTITY_LINE))) {
            event.setSignLine(QUANTITY_LINE, Integer.toString(quantity));
        }

        if (!Properties.USE_STOCK_COUNTER || ChestShopSign.isAdminShop(event.getSignLine(NAME_LINE))) {
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            return;
        }

        ItemStack itemTradedByShop = determineItemTradedByShop(event.getSignLine(ITEM_LINE));
        if (itemTradedByShop != null) {
            Container container = uBlock.findConnectedContainer(event.getSign());
            if (container != null) {
                event.setSignLine(QUANTITY_LINE, getQuantityLineWithCounter(quantity, itemTradedByShop, container.getInventory()));
            }
        }
    }

    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getLocation() == null || !ChestShopSign.isShopBlock(event.getInventory().getLocation().getBlock())) {
            return;
        }

        for (Sign shopSign : uBlock.findConnectedShopSigns(event.getInventory().getHolder())) {
            if (ChestShopSign.isAdminShop(shopSign)) {
                return;
            }

            if (!Properties.USE_STOCK_COUNTER) {
                if (QuantityUtil.quantityLineContainsCounter(shopSign.getLine(QUANTITY_LINE))) {
                    removeCounterFromQuantityLine(shopSign);
                }
                continue;
            }

            if (Properties.MAX_SHOP_AMOUNT > 99999) {
                ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
                if (QuantityUtil.quantityLineContainsCounter(shopSign.getLine(QUANTITY_LINE))) {
                    removeCounterFromQuantityLine(shopSign);
                }
                return;
            }

            updateCounterOnQuantityLine(shopSign, event.getInventory());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void onTransaction(final TransactionEvent event) {
        if (!Properties.USE_STOCK_COUNTER) {
            if (QuantityUtil.quantityLineContainsCounter(event.getSign().getLine(QUANTITY_LINE))) {
                removeCounterFromQuantityLine(event.getSign());
            }
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            if (QuantityUtil.quantityLineContainsCounter(event.getSign().getLine(QUANTITY_LINE))) {
                removeCounterFromQuantityLine(event.getSign());
            }
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSign())) {
            return;
        }

        for (Sign shopSign : uBlock.findConnectedShopSigns(event.getOwnerInventory().getHolder())) {
            updateCounterOnQuantityLine(shopSign, event.getOwnerInventory());
        }
    }

    public static void updateCounterOnQuantityLine(Sign sign, Inventory chestShopInventory) {
        ItemStack itemTradedByShop = determineItemTradedByShop(sign);
        if (itemTradedByShop == null) {
            return;
        }

        int quantity;
        try {
            quantity = QuantityUtil.parseQuantity(sign.getLine(QUANTITY_LINE));
        } catch (IllegalFormatException invalidQuantity) {
            return;
        }

        int numTradedItemsInChest = InventoryUtil.getAmount(itemTradedByShop, chestShopInventory);

        sign.setLine(QUANTITY_LINE, String.format(PRICE_LINE_WITH_COUNT, quantity, numTradedItemsInChest));
        sign.update(true);
    }

    public static void removeCounterFromQuantityLine(Sign sign) {
        int quantity;
        try {
            quantity = QuantityUtil.parseQuantity(sign.getLine(QUANTITY_LINE));
        } catch (IllegalFormatException invalidQuantity) {
            return;
        }

        sign.setLine(QUANTITY_LINE, Integer.toString(quantity));
        sign.update(true);
    }

    public static String getQuantityLineWithCounter(int amount, ItemStack itemTransacted, Inventory chestShopInventory) {
        int numTransactionItemsInChest = InventoryUtil.getAmount(itemTransacted, chestShopInventory);

        return String.format(PRICE_LINE_WITH_COUNT, amount, numTransactionItemsInChest);
    }

    public static ItemStack determineItemTradedByShop(Sign sign) {
        return determineItemTradedByShop(sign.getLine(ITEM_LINE));
    }

    public static ItemStack determineItemTradedByShop(String material) {
        ItemParseEvent parseEvent = new ItemParseEvent(material);
        Bukkit.getPluginManager().callEvent(parseEvent);
        return parseEvent.getItem();
    }
}
