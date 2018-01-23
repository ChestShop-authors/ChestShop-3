package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

        Player client = event.getClient();

        double pricePerItem = event.getPrice() / InventoryUtil.countItems(event.getStock());

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(client);
        ChestShop.callEvent(currencyAmountEvent);

        BigDecimal walletMoney = currencyAmountEvent.getAmount();

        CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(BigDecimal.valueOf(event.getPrice()), client);
        ChestShop.callEvent(currencyCheckEvent);

        if (!currencyCheckEvent.hasEnough()) {
            int amountAffordable = getAmountOfAffordableItems(walletMoney, pricePerItem);

            if (amountAffordable < 1) {
                event.setCancelled(CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY);
                return;
            }

            event.setPrice(amountAffordable * pricePerItem);
            event.setStock(getCountedItemStack(event.getStock(), amountAffordable));
        }
        
        if (!InventoryUtil.hasItems(event.getStock(), event.getOwnerInventory())) {
            ItemStack[] itemsHad = getItems(event.getStock(), event.getOwnerInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsHad);
        
            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_STOCK_IN_CHEST);
                return;
            }
        
            event.setPrice(pricePerItem * possessedItemCount);
            event.setStock(itemsHad);
        }
    
        if (!InventoryUtil.fits(event.getStock(), event.getClientInventory())) {
            ItemStack[] itemsFit = getItemsThatFit(event.getStock(), event.getClientInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsFit);
            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_SPACE_IN_INVENTORY);
                return;
            }
            
            event.setStock(itemsFit);
            event.setPrice(pricePerItem * possessedItemCount);
        }
        
        UUID seller = event.getOwnerAccount().getUuid();

        CurrencyHoldEvent currencyHoldEvent = new CurrencyHoldEvent(BigDecimal.valueOf(event.getPrice()), seller, client.getWorld());
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

        Player client = event.getClient();
        UUID owner = event.getOwnerAccount().getUuid();

        double pricePerItem = event.getPrice() / InventoryUtil.countItems(event.getStock());

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(owner, client.getWorld());
        ChestShop.callEvent(currencyAmountEvent);

        BigDecimal walletMoney = currencyAmountEvent.getAmount();

        if (Economy.isOwnerEconomicallyActive(event.getOwnerInventory())) {
            CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(BigDecimal.valueOf(event.getPrice()), owner, client.getWorld());
            ChestShop.callEvent(currencyCheckEvent);

            if (!currencyCheckEvent.hasEnough()) {
                int amountAffordable = getAmountOfAffordableItems(walletMoney, pricePerItem);

                if (amountAffordable < 1) {
                    event.setCancelled(SHOP_DOES_NOT_HAVE_ENOUGH_MONEY);
                    return;
                }

                event.setPrice(amountAffordable * pricePerItem);
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
        
            event.setPrice(pricePerItem * possessedItemCount);
            event.setStock(itemsHad);
        }
    
        if (!InventoryUtil.fits(event.getStock(), event.getOwnerInventory())) {
            ItemStack[] itemsFit = getItemsThatFit(event.getStock(), event.getOwnerInventory());
            int possessedItemCount = InventoryUtil.countItems(itemsFit);
            if (possessedItemCount <= 0) {
                event.setCancelled(NOT_ENOUGH_SPACE_IN_CHEST);
                return;
            }
    
            event.setStock(itemsFit);
            event.setPrice(pricePerItem * possessedItemCount);
        }

        CurrencyHoldEvent currencyHoldEvent = new CurrencyHoldEvent(BigDecimal.valueOf(event.getPrice()), client);
        ChestShop.callEvent(currencyHoldEvent);

        if (!currencyHoldEvent.canHold()) {
            event.setCancelled(CLIENT_DEPOSIT_FAILED);
        }
    }

    private static int getAmountOfAffordableItems(BigDecimal walletMoney, double pricePerItem) {
        return (int) Math.floor(walletMoney.doubleValue() / pricePerItem);
    }

    private static ItemStack[] getItems(ItemStack[] stock, Inventory inventory) {
        List<ItemStack> toReturn = new LinkedList<>();

        for (ItemStack item : InventoryUtil.mergeSimilarStacks(stock)) {
            int amount = InventoryUtil.getAmount(item, inventory);

            Collections.addAll(toReturn, getCountedItemStack(new ItemStack[]{item},
                    amount > item.getAmount() ? item.getAmount() : amount));
        }

        return toReturn.toArray(new ItemStack[toReturn.size()]);
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

        return stacks.toArray(new ItemStack[stacks.size()]);
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
    
        for (ItemStack item : InventoryUtil.mergeSimilarStacks(stock)) {
            int maxStackSize = InventoryUtil.getMaxStackSize(item);
            int free = 0;
            for (ItemStack itemInInventory : inventory.getContents()) {
                if (MaterialUtil.equals(item, itemInInventory)) {
                    free += (maxStackSize - itemInInventory.getAmount()) % maxStackSize;
                }
            }
    
            if (free == 0 && emptySlots == 0) {
                continue;
            }
    
            if (item.getAmount() > free) {
                if (emptySlots > 0) {
                    int requiredSlots = (int) Math.ceil(((double) item.getAmount() - free) / maxStackSize);
                    if (requiredSlots <= emptySlots) {
                        emptySlots = emptySlots - requiredSlots;
                    } else {
                        item.setAmount(free + maxStackSize * emptySlots);
                        emptySlots = 0;
                    }
                } else {
                    item.setAmount(free);
                }
            }
            Collections.addAll(resultStock, InventoryUtil.getItemsStacked(item));
        }
        
        return resultStock.toArray(new ItemStack[resultStock.size()]);
    }
}
