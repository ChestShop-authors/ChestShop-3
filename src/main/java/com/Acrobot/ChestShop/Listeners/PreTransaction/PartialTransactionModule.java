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
import java.util.ArrayList;
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onPreBuyTransaction(PreTransactionEvent event) {
        if (event.getTransactionType() != BUY) {
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
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onPreSellTransaction(PreTransactionEvent event) {
        if (event.getTransactionType() != SELL) {
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
                event.setCancelled(NOT_ENOUGH_STOCK_IN_CHEST);
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
        List<ItemStack> toReturn = new LinkedList<ItemStack>();

        ItemStack[] neededItems = InventoryUtil.mergeSimilarStacks(stock);

        for (ItemStack item : neededItems) {
            int amount = InventoryUtil.getAmount(item, inventory);

            ItemStack clone = item.clone();
            clone.setAmount(amount > item.getAmount() ? item.getAmount() : amount);

            toReturn.add(clone);
        }

        return toReturn.toArray(new ItemStack[toReturn.size()]);
    }

    private static ItemStack[] getCountedItemStack(ItemStack[] stock, int numberOfItems) {
        int left = numberOfItems;
        LinkedList<ItemStack> stacks = new LinkedList<ItemStack>();

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

            for (ItemStack iStack : stacks) {
                if (MaterialUtil.equals(toAdd, iStack)) {
                    iStack.setAmount(iStack.getAmount() + toAdd.getAmount());
                    added = true;
                    break;
                }
            }

            if (!added) {
                stacks.add(toAdd);
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
        List<ItemStack> resultStock = new ArrayList<>();
        
        int emptySlots = InventoryUtil.countEmpty(inventory);
        ItemStack[] itemsInInventory = getItems(stock, inventory);
    
        for (ItemStack item : stock) {
            int maxStackSize = InventoryUtil.getMaxStackSize(item);
            int free = 0;
            for (ItemStack itemInInventory : itemsInInventory) {
                if (MaterialUtil.equals(item, itemInInventory)) {
                    free = (maxStackSize - itemInInventory.getAmount()) % maxStackSize;
                    break;
                }
            }
    
            if (free == 0 && emptySlots == 0) {
                continue;
            }
    
            ItemStack clone = item.clone();
            if (item.getAmount() > free) {
                if (emptySlots > 0) {
                    int requiredSlots = (int) Math.ceil((item.getAmount() - free) / maxStackSize);
                    if (requiredSlots <= emptySlots) {
                        emptySlots = emptySlots - requiredSlots;
                    } else {
                        emptySlots = 0;
                        clone.setAmount(free + maxStackSize * emptySlots);
                    }
                } else {
                    clone.setAmount(free);
                }
            }
            resultStock.add(clone);
        }
        
        return (ItemStack[]) resultStock.toArray();
    }
}
