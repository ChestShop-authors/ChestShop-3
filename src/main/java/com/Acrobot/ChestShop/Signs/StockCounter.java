package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.IllegalFormatException;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;

/**
 * @author bricefrisco
 */
public class StockCounter {
    private static final String PRICE_LINE_WITH_COUNT = "Q %d : C %d";

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
