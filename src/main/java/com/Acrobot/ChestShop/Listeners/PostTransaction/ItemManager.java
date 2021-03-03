package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.ImplementationAdapter;
import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;

/**
 * @author Acrobot
 */
public class ItemManager implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void shopItemRemover(TransactionEvent event) {
        if (event.getTransactionType() != BUY) {
            return;
        }

        transferItems(event.getOwnerInventory(), event.getClientInventory(), event.getStock());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void inventoryItemRemover(TransactionEvent event) {
        if (event.getTransactionType() != SELL) {
            return;
        }

        transferItems(event.getClientInventory(), event.getOwnerInventory(), event.getStock());
    }

    private static void transferItems(Inventory sourceInventory, Inventory targetInventory, ItemStack[] items) {
        if (Properties.STACK_TO_64) {
            for (ItemStack item : items) {
                InventoryUtil.transfer(item, sourceInventory, targetInventory, 64);
            }
        } else {
            for (ItemStack item : items) {
                InventoryUtil.transfer(item, sourceInventory, targetInventory);
            }
        }
        update(sourceInventory.getHolder());
        update(targetInventory.getHolder());
    }

    private static void update(InventoryHolder holder) {
        if (holder instanceof Player) {
            ((Player) holder).updateInventory();
        } else if (holder instanceof BlockState) {
            ((BlockState) holder).update();
        } else if (holder instanceof DoubleChest) {
            update(ImplementationAdapter.getLeftSide((DoubleChest) holder, false));
            update(ImplementationAdapter.getRightSide((DoubleChest) holder, false));
        }
    }
}
