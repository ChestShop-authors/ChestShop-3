package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.StockUpdateEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.IllegalFormatException;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.QUANTITY_LINE;

/**
 * @author bjorn-out
 */
public class StockUpdateListener implements Listener {
    private static final String PRICE_LINE_WITH_COUNT = "Q %d : C %d";

    @EventHandler(priority = EventPriority.LOW)
    public static void onStockUpdate(StockUpdateEvent event) {
        int quantity;
        try {
            quantity = ChestShopSign.getQuantity(event.getSign());
        } catch (IllegalFormatException invalidQuantity) {
            return;
        }

        if (Properties.FORCE_UNLIMITED_ADMIN_SHOP && ChestShopSign.isAdminShop(event.getSign().getLines())) {
            removeCounter(event.getSign(), quantity);
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            removeCounter(event.getSign(), quantity);
            return;
        }

        event.getSign().setLine(QUANTITY_LINE, String.format(PRICE_LINE_WITH_COUNT, quantity, event.getStock()));
        event.getSign().update(true);
    }

    private static void removeCounter(Sign sign, int quantity) {
        if (!QuantityUtil.quantityLineContainsCounter(ChestShopSign.getQuantityLine(sign.getLines()))) {
            return;
        }

        sign.setLine(QUANTITY_LINE, Integer.toString(quantity));
        sign.update(true);
    }
}
