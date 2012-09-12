package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.ChestShop.Config.Property.REMOVE_EMPTY_CHESTS;
import static com.Acrobot.ChestShop.Config.Property.REMOVE_EMPTY_SHOPS;

/**
 * @author Acrobot
 */
public class EmptyShopDeleter implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() != TransactionEvent.TransactionType.BUY) {
            return;
        }

        Inventory ownerInventory = event.getOwnerInventory();
        Sign sign = event.getSign();

        if (!shopShouldBeRemoved(ownerInventory, event.getStock())) {
            return;
        }

        sign.getBlock().setType(Material.AIR);

        if (Config.getBoolean(REMOVE_EMPTY_CHESTS) && !ChestShopSign.isAdminShop(sign) && chestIsEmpty(event.getOwnerInventory())) {
            Chest connectedChest = uBlock.findConnectedChest(sign);
            connectedChest.getBlock().setType(Material.AIR);
        } else {
            ownerInventory.addItem(new ItemStack(Material.SIGN, 1));
        }
    }

    private static boolean chestIsEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                return false;
            }
        }

        return true;
    }

    private static boolean shopShouldBeRemoved(Inventory inventory, ItemStack[] stock) {
        return Config.getBoolean(REMOVE_EMPTY_SHOPS) && !hasMoreStock(inventory, stock);
    }

    private static boolean hasMoreStock(Inventory inventory, ItemStack[] stock) {
        for (ItemStack stack : stock) {
            if (InventoryUtil.getAmount(stack, inventory) > 0) {
                return true;
            }
        }

        return false;
    }
}
