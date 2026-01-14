package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.StockUpdateEvent;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getHolder;
import static org.bukkit.Bukkit.getServer;


/**
 * @author bricefrisco
 */
public class StockCounterModule implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        if (!StockUpdateEvent.hasHandlers()) { return; }
        Container container = uBlock.findConnectedContainer(event.getSign());
        if (container == null) { return; }

        Bukkit.getScheduler().runTask(ChestShop.getPlugin(), () ->
                fireStockUpdateEvents(container.getInventory()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (!StockUpdateEvent.hasHandlers()) { return; }

        if (event.getInventory().getType() == InventoryType.ENDER_CHEST || event.getInventory().getLocation() == null) {
            return;
        }

        InventoryHolder holder = getHolder(event.getInventory(), false);
        if (!uBlock.couldBeShopContainer(holder)) { return; }

        fireStockUpdateEvents(event.getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTransaction(final TransactionEvent event) {
        if (!StockUpdateEvent.hasHandlers()) { return; }
        fireStockUpdateEvents(event.getOwnerInventory());
    }

    public static void fireStockUpdateEvents(Inventory inventory) {
        for (Sign sign : uBlock.findConnectedShopSigns( getHolder(inventory, false))) {
            ItemStack itemTradedByShop = determineItemTradedByShop(sign);
            if (itemTradedByShop == null) {
                return;
            }
            int stock = InventoryUtil.getAmount(itemTradedByShop, inventory);

            StockUpdateEvent updateEvent = new StockUpdateEvent(stock, sign);
            getServer().getPluginManager().callEvent(updateEvent);
        }
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
