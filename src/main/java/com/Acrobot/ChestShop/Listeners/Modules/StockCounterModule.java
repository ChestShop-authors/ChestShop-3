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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.IllegalFormatException;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getHolder;
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
            quantity = ChestShopSign.getQuantity(event.getSignLines());
        } catch (IllegalArgumentException invalidQuantity) {
            return;
        }

        if (QuantityUtil.quantityLineContainsCounter(ChestShopSign.getQuantityLine(event.getSignLines()))) {
            event.setSignLine(QUANTITY_LINE, Integer.toString(quantity));
        }

        if (!Properties.USE_STOCK_COUNTER
                || (Properties.FORCE_UNLIMITED_ADMIN_SHOP && ChestShopSign.isAdminShop(event.getSignLines()))) {
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            return;
        }

        ItemStack itemTradedByShop = determineItemTradedByShop(ChestShopSign.getItem(event.getSignLines()));
        if (itemTradedByShop != null) {
            Container container = uBlock.findConnectedContainer(event.getSign());
            if (container != null) {
                event.setSignLine(QUANTITY_LINE, getQuantityLineWithCounter(quantity, itemTradedByShop, container.getInventory()));
            }
        }
    }

    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST || event.getInventory().getLocation() == null || !uBlock.couldBeShopContainer(event.getInventory().getLocation().getBlock())) {
            return;
        }

        for (Sign shopSign : uBlock.findConnectedShopSigns(getHolder(event.getInventory(), false))) {
            if (!Properties.USE_STOCK_COUNTER
                    || (Properties.FORCE_UNLIMITED_ADMIN_SHOP && ChestShopSign.isAdminShop(shopSign))) {
                if (QuantityUtil.quantityLineContainsCounter(ChestShopSign.getQuantityLine(shopSign))) {
                    removeCounterFromQuantityLine(shopSign);
                }
                continue;
            }

            if (Properties.MAX_SHOP_AMOUNT > 99999) {
                ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
                if (QuantityUtil.quantityLineContainsCounter(ChestShopSign.getQuantityLine(shopSign))) {
                    removeCounterFromQuantityLine(shopSign);
                }
                return;
            }

            updateCounterOnQuantityLine(shopSign, event.getInventory());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void onTransaction(final TransactionEvent event) {
        String quantityLine = ChestShopSign.getQuantityLine(event.getSign());
        if (!Properties.USE_STOCK_COUNTER) {
            if (QuantityUtil.quantityLineContainsCounter(quantityLine)) {
                removeCounterFromQuantityLine(event.getSign());
            }
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            if (QuantityUtil.quantityLineContainsCounter(quantityLine)) {
                removeCounterFromQuantityLine(event.getSign());
            }
            return;
        }

        if (Properties.FORCE_UNLIMITED_ADMIN_SHOP && ChestShopSign.isAdminShop(event.getSign())) {
            return;
        }

        for (Sign shopSign : uBlock.findConnectedShopSigns( getHolder(event.getOwnerInventory(), false))) {
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
            quantity = ChestShopSign.getQuantity(sign);
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
            quantity = ChestShopSign.getQuantity(sign);
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
        return determineItemTradedByShop(ChestShopSign.getItem(sign));
    }

    public static ItemStack determineItemTradedByShop(String material) {
        ItemParseEvent parseEvent = new ItemParseEvent(material);
        Bukkit.getPluginManager().callEvent(parseEvent);
        return parseEvent.getItem();
    }
}
