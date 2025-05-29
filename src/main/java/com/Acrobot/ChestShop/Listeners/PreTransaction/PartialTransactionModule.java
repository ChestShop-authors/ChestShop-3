package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyHoldEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.*;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

/**
 * @author Acrobot
 */
public class PartialTransactionModule implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreBuyTransaction(PreTransactionEvent event) {
        if (event.isCancelled() || event.getTransactionType() != BUY) {
            return;
        }

        int itemCount = InventoryUtil.countItems(event.getStock());
        if (itemCount <= 0) {
            return;
        }

        Player client = event.getClient();

        BigDecimal pricePerItem = event.getExactPrice().divide(BigDecimal.valueOf(itemCount), MathContext.DECIMAL128);

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(client);
        ChestShop.callEvent(currencyAmountEvent);

        BigDecimal walletMoney = currencyAmountEvent.getAmount();

        CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(event.getExactPrice(), client);
        ChestShop.callEvent(currencyCheckEvent);

        if (!currencyCheckEvent.hasEnough()) {
            int amountAffordable = getAmountOfAffordableItems(walletMoney, pricePerItem);

            if (amountAffordable < 1) {
                event.setCancelled(CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY);
                return;
            }

            BigDecimal pricePerItemScaled = pricePerItem.multiply(new BigDecimal(amountAffordable)).setScale(Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
            if (pricePerItem.compareTo(BigDecimal.ZERO) > 0 && pricePerItemScaled.compareTo(BigDecimal.ZERO) == 0) {
                event.setCancelled(CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY);
                return;
            }

            event.setExactPrice(pricePerItemScaled);
            event.setStock(getCountedItemStack(event.getStock(), amountAffordable));
        }

        if (!InventoryUtil.hasItems(event.getStock(), event.getOwnerInventory())) {
            ItemStack[] itemsHad = getItems(event.getStock(), event.getOwnerInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsHad);

            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_CHEST);
                return;
            }

            BigDecimal pricePerItemScaled = pricePerItem.multiply(new BigDecimal(possessedItemCount)).setScale(Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
            if (pricePerItem.compareTo(BigDecimal.ZERO) > 0 && pricePerItemScaled.compareTo(BigDecimal.ZERO) == 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_CHEST);
                return;
            }

            event.setExactPrice(pricePerItemScaled);
            event.setStock(itemsHad);
        }

        if (!InventoryUtil.fits(event.getStock(), event.getClientInventory())) {
            ItemStack[] itemsFit = getItemsThatFit(event.getStock(), event.getClientInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsFit);
            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_SPACE_IN_INVENTORY);
                return;
            }

            BigDecimal pricePerItemScaled = pricePerItem.multiply(new BigDecimal(possessedItemCount)).setScale(Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
            if (pricePerItem.compareTo(BigDecimal.ZERO) > 0 && pricePerItemScaled.compareTo(BigDecimal.ZERO) == 0) {
                event.setCancelled(NOT_ENOUGH_SPACE_IN_INVENTORY);
                return;
            }

            event.setExactPrice(pricePerItemScaled);
            event.setStock(itemsFit);
        }

        UUID seller = event.getOwnerAccount().getUuid();

        CurrencyHoldEvent currencyHoldEvent = new CurrencyHoldEvent(event.getExactPrice(), seller, client.getWorld());
        ChestShop.callEvent(currencyHoldEvent);

        if (!currencyHoldEvent.canHold()) {
            event.setCancelled(SHOP_DEPOSIT_FAILED);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreSellTransaction(PreTransactionEvent event) {
        if (event.isCancelled() || event.getTransactionType() != SELL) {
            return;
        }

        int itemCount = InventoryUtil.countItems(event.getStock());
        if (itemCount <= 0) {
            return;
        }

        Player client = event.getClient();
        UUID owner = event.getOwnerAccount().getUuid();

        BigDecimal pricePerItem = event.getExactPrice().divide(BigDecimal.valueOf(itemCount), MathContext.DECIMAL128);


        if (Economy.isOwnerEconomicallyActive(event.getOwnerInventory())) {
            CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(event.getExactPrice(), owner, client.getWorld());
            ChestShop.callEvent(currencyCheckEvent);

            if (!currencyCheckEvent.hasEnough()) {
                CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(owner, client.getWorld());
                ChestShop.callEvent(currencyAmountEvent);

                BigDecimal walletMoney = currencyAmountEvent.getAmount();
                int amountAffordable = getAmountOfAffordableItems(walletMoney, pricePerItem);

                if (amountAffordable < 1) {
                    event.setCancelled(SHOP_DOES_NOT_HAVE_ENOUGH_MONEY);
                    return;
                }

                BigDecimal pricePerItemScaled = pricePerItem.multiply(new BigDecimal(amountAffordable)).setScale(Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
                if (pricePerItem.compareTo(BigDecimal.ZERO) > 0 && pricePerItemScaled.compareTo(BigDecimal.ZERO) == 0) {
                    event.setCancelled(SHOP_DOES_NOT_HAVE_ENOUGH_MONEY);
                    return;
                }

                event.setExactPrice(pricePerItemScaled);
                event.setStock(getCountedItemStack(event.getStock(), amountAffordable));
            }
        }

        if (!InventoryUtil.hasItems(event.getStock(), event.getClientInventory())) {
            ItemStack[] itemsHad = getItems(event.getStock(), event.getClientInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsHad);

            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_INVENTORY);
                return;
            }

            BigDecimal pricePerItemScaled = pricePerItem.multiply(new BigDecimal(possessedItemCount)).setScale(Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
            if (pricePerItem.compareTo(BigDecimal.ZERO) > 0 && pricePerItemScaled.compareTo(BigDecimal.ZERO) == 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_INVENTORY);
                return;
            }

            event.setExactPrice(pricePerItemScaled);
            event.setStock(itemsHad);
        }

        if (!InventoryUtil.fits(event.getStock(), event.getOwnerInventory())) {
            ItemStack[] itemsFit = getItemsThatFit(event.getStock(), event.getOwnerInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsFit);
            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_SPACE_IN_CHEST);
                return;
            }

            BigDecimal pricePerItemScaled = pricePerItem.multiply(new BigDecimal(possessedItemCount)).setScale(Properties.PRICE_PRECISION, BigDecimal.ROUND_HALF_UP);
            if (pricePerItem.compareTo(BigDecimal.ZERO) > 0 && pricePerItemScaled.compareTo(BigDecimal.ZERO) == 0) {
                event.setCancelled(NOT_ENOUGH_SPACE_IN_CHEST);
                return;
            }

            event.setExactPrice(pricePerItemScaled);
            event.setStock(itemsFit);
        }

        CurrencyHoldEvent currencyHoldEvent = new CurrencyHoldEvent(event.getExactPrice(), client);
        ChestShop.callEvent(currencyHoldEvent);

        if (!currencyHoldEvent.canHold()) {
            event.setCancelled(CLIENT_DEPOSIT_FAILED);
        }
    }

    private static int getAmountOfAffordableItems(BigDecimal walletMoney, BigDecimal pricePerItem) {
        return walletMoney.divide(pricePerItem, 0, RoundingMode.FLOOR).intValueExact();
    }

    private static ItemStack[] getItems(ItemStack[] stock, Inventory inventory) {
        List<ItemStack> toReturn = new LinkedList<>();

        for (Map.Entry<ItemStack, Integer> entry : InventoryUtil.getItemCounts(stock).entrySet()) {
            int amount = InventoryUtil.getAmount(entry.getKey(), inventory);

            Collections.addAll(toReturn, getCountedItemStack(new ItemStack[]{entry.getKey()},
                    Math.min(amount, entry.getValue())));
        }

        return toReturn.toArray(new ItemStack[0]);
    }

    private static ItemStack[] getCountedItemStack(ItemStack[] stock, int numberOfItems) {
        int left = numberOfItems;
        LinkedList<ItemStack> stacks = new LinkedList<>();

        for (ItemStack stack : stock) {
            int count = stack.getAmount();
            ItemStack toAdd;

            if (left > count) {
                toAdd = stack;
                left -= count;
            } else {
                ItemStack clone = stack.clone();

                clone.setAmount(left);
                toAdd = clone;
                left = 0;
            }

            boolean added = false;

            int maxStackSize = InventoryUtil.getMaxStackSize(stack);

            for (ItemStack iStack : stacks) {
                if (iStack.getAmount() < maxStackSize && MaterialUtil.equals(toAdd, iStack)) {
                    int newAmount = iStack.getAmount() + toAdd.getAmount();
                    if (newAmount > maxStackSize) {
                        iStack.setAmount(maxStackSize);
                        toAdd.setAmount(newAmount - maxStackSize);
                    } else {
                        iStack.setAmount(newAmount);
                        added = true;
                    }
                    break;
                }
            }

            if (!added) {
                Collections.addAll(stacks, InventoryUtil.getItemsStacked(toAdd));
            }

            if (left <= 0) {
                break;
            }
        }

        return stacks.toArray(new ItemStack[0]);
    }

    /**
     * Make an array of items fit into an inventory.
     *
     * @param stock     The items to fit in the inventory
     * @param inventory The inventory to fit it in
     * @return Whether or not the items fit into the inventory
     */
    private static ItemStack[] getItemsThatFit(ItemStack[] stock, Inventory inventory) {
        List<ItemStack> resultStock = new LinkedList<>();

        int emptySlots = InventoryUtil.countEmpty(inventory);

        for (Map.Entry<ItemStack, Integer> entry : InventoryUtil.getItemCounts(stock).entrySet()) {
            ItemStack item = entry.getKey();
            int amount = entry.getValue();
            int maxStackSize = InventoryUtil.getMaxStackSize(item);
            int free = 0;
            for (ItemStack itemInInventory : inventory.getContents()) {
                if (MaterialUtil.equals(item, itemInInventory)) {
                    if (itemInInventory != null) {
                        free += (maxStackSize - itemInInventory.getAmount()) % maxStackSize;
                    }
                }
            }

            if (free == 0 && emptySlots == 0) {
                continue;
            }

            if (amount > free) {
                if (emptySlots > 0) {
                    int requiredSlots = (int) Math.ceil(((double) amount - free) / maxStackSize);
                    if (requiredSlots <= emptySlots) {
                        emptySlots = emptySlots - requiredSlots;
                    } else {
                        amount = free + maxStackSize * emptySlots;
                        emptySlots = 0;
                    }
                } else {
                    amount = free;
                }
            }
            Collections.addAll(resultStock, InventoryUtil.getItemStacked(item, amount));
        }

        return resultStock.toArray(new ItemStack[0]);
    }
}
