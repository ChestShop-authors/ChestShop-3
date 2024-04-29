package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.ChestShop.Listeners.Modules.StockCounterModule;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getHolder;

/**
 * @author Acrobot
 */
public class ItemMoveListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onItemMove(InventoryMoveItemEvent event) {
        InventoryHolder destinationHolder = getHolder(event.getDestination(), false);
        InventoryHolder sourceHolder = getHolder(event.getSource(), false);

        if (!(destinationHolder instanceof BlockState) && ChestShopSign.isShopBlock(sourceHolder)) {
            event.setCancelled(true);
        } else if (ChestShopSign.isShopBlock(destinationHolder) && sourceHolder instanceof Hopper) {
            Block shopBlock = ChestShopSign.getShopBlock(destinationHolder);
            Sign connectedSign = uBlock.getConnectedSign(shopBlock);

            Inventory tempInv = Bukkit.createInventory(null, destinationHolder.getInventory().getSize() + 9);
            tempInv.setContents(ItemUtil.deepClone(destinationHolder.getInventory().getContents()));
            tempInv.addItem(event.getItem().clone());

            StockCounterModule.updateCounterOnQuantityLine(connectedSign, tempInv);

            tempInv.clear();
            tempInv.close();
        }
    }


}
