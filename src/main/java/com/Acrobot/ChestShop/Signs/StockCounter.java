package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

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
        int amount;
        try {
            amount = QuantityUtil.parseQuantity(sign.getLine(QUANTITY_LINE));
        } catch (IllegalFormatException invalidQuantity) {
            return;
        }

        sign.setLine(QUANTITY_LINE, Integer.toString(amount));
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

    public static List<Sign> findNearbyShopSigns(InventoryHolder chestShopInventoryHolder) {
        List<Sign> result = new ArrayList<>();

        if (chestShopInventoryHolder instanceof DoubleChest) {
            BlockState leftChestSide = (BlockState) ((DoubleChest) chestShopInventoryHolder).getLeftSide();
            BlockState rightChestSide = (BlockState) ((DoubleChest) chestShopInventoryHolder).getRightSide();

            if (leftChestSide == null || rightChestSide == null) {
                return result;
            }

            Block leftChest = leftChestSide.getBlock();
            Block rightChest = rightChestSide.getBlock();

            if (ChestShopSign.isShopBlock(leftChest)) {
                result.addAll(uBlock.findAllNearbyShopSigns(leftChest));
            }

            if (ChestShopSign.isShopBlock(rightChest)) {
                result.addAll(uBlock.findAllNearbyShopSigns(rightChest));
            }
        }

        else if (chestShopInventoryHolder instanceof BlockState) {
            Block chestBlock = ((BlockState) chestShopInventoryHolder).getBlock();

            if (ChestShopSign.isShopBlock(chestBlock)) {
                result.addAll(uBlock.findAllNearbyShopSigns(chestBlock));
            }
        }

        return result;
    }
}
