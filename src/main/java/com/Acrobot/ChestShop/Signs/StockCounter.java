package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.IllegalFormatException;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;

/**
 * @author bricefrisco
 */
public class StockCounter {
    private static final String PRICE_LINE_WITH_COUNT = "Q %d : C %d";

    public static void updateCounterOnQuantityLine(Sign sign, ItemStack itemTransacted, Inventory chestShopInventory) {
        int numTransactionItemsInChest = InventoryUtil.getAmount(itemTransacted, chestShopInventory);

        int amount;
        try {
            amount = QuantityUtil.parseQuantity(sign.getLine(QUANTITY_LINE));
        } catch (IllegalFormatException invalidQuantity) {
            return;
        }

        sign.setLine(QUANTITY_LINE, String.format(PRICE_LINE_WITH_COUNT, amount, numTransactionItemsInChest));
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

    public static Sign determineChestShopSign(InventoryHolder chestShopInventoryHolder) {
        Block chestShopBlock = determineChestShopBlock(chestShopInventoryHolder);
        return determineChestShopSign(chestShopBlock);
    }

    public static Sign determineChestShopSign(Block chestShopBlock) {
        if (chestShopBlock == null) {
            return null;
        }
        return uBlock.getConnectedSign(chestShopBlock);
    }

    public static Block determineChestShopBlock(InventoryHolder chestShopInventoryHolder) {
        if (chestShopInventoryHolder instanceof DoubleChest) {
            BlockState leftChestSide = (BlockState) ((DoubleChest) chestShopInventoryHolder).getLeftSide();
            BlockState rightChestSide = (BlockState) ((DoubleChest) chestShopInventoryHolder).getRightSide();

            if (leftChestSide == null || rightChestSide == null) {
                return null;
            }

            Block leftChest = leftChestSide.getBlock();
            Block rightChest = rightChestSide.getBlock();

            if (ChestShopSign.isShopBlock(leftChest)) {
                return leftChest;
            } else if (ChestShopSign.isShopBlock(rightChest)) {
                return rightChest;
            }
        }

        if (chestShopInventoryHolder instanceof BlockState) {
            Block chestBlock = ((BlockState) chestShopInventoryHolder).getBlock();

            if (ChestShopSign.isShopBlock(chestBlock)) {
                return chestBlock;
            }
        }

        return null;
    }
}
