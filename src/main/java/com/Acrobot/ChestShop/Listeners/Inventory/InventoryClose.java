package com.Acrobot.ChestShop.Listeners.Inventory;

import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Signs.StockCounter;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;

/**
 * @author bricefrisco
 */
public class InventoryClose implements Listener {

    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (!ChestShopSign.isShopBlock(event.getInventory().getHolder())) {
            return;
        }

        for (Sign shopSign : uBlock.findConnectedShopSigns(event.getInventory().getHolder())) {
            if (ChestShopSign.isAdminShop(shopSign)) {
                return;
            }

            if (!Properties.USE_STOCK_COUNTER) {
                if (QuantityUtil.quantityLineContainsCounter(shopSign.getLine(QUANTITY_LINE))) {
                    StockCounter.removeCounterFromQuantityLine(shopSign);
                }
                continue;
            }

            if (Properties.MAX_SHOP_AMOUNT > 99999) {
                ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
                if (QuantityUtil.quantityLineContainsCounter(shopSign.getLine(QUANTITY_LINE))) {
                    StockCounter.removeCounterFromQuantityLine(shopSign);
                }
                return;
            }

            StockCounter.updateCounterOnQuantityLine(shopSign, event.getInventory());
        }
    }
}
