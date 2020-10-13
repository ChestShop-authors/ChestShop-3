package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Signs.StockCounter;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;

/**
 * @author bricefrisco
 */
public class StockCounterManager implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public static void onTransaction(final TransactionEvent event) {
        if (!Properties.USE_STOCK_COUNTER) {
            if (QuantityUtil.quantityLineContainsCounter(event.getSign().getLine(QUANTITY_LINE))) {
                StockCounter.removeCounterFromQuantityLine(event.getSign());
            }
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSign()) || event.getStock().length == 0) {
            return;
        }

        for (Sign shopSign : StockCounter.findNearbyShopSigns(event.getOwnerInventory().getHolder())) {
            Bukkit.getLogger().info("Updating stock counter.");
            StockCounter.updateCounterOnQuantityLine(shopSign, event.getOwnerInventory());
        }
    }
}
