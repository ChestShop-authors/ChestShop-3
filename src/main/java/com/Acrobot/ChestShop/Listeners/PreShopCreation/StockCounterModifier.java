package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Signs.StockCounter;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;

/**
 * @author bricefrisco
 */
public class StockCounterModifier implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        int amount;
        try {
            amount = QuantityUtil.parseQuantity(event.getSignLine(QUANTITY_LINE));
        } catch (IllegalArgumentException e) {
            return;
        }

        if (!Properties.USE_STOCK_COUNTER) {
            if (QuantityUtil.quantityLineContainsCounter(event.getSignLine(QUANTITY_LINE))) {
                event.setSignLine(QUANTITY_LINE, Integer.toString(amount));
            }
            return;
        }

        if (Properties.MAX_SHOP_AMOUNT > 99999) {
            ChestShop.getBukkitLogger().warning("Stock counter cannot be used if MAX_SHOP_AMOUNT is over 5 digits");
            if (QuantityUtil.quantityLineContainsCounter(event.getSignLine(QUANTITY_LINE))) {
                event.setSignLine(QUANTITY_LINE, Integer.toString(amount));
            }
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSignLine(NAME_LINE))) {
            return;
        }

        ItemStack itemTradedByShop = StockCounter.determineItemTradedByShop(event.getSignLine(ITEM_LINE));
        Inventory chestShopInventory = uBlock.findConnectedContainer(event.getSign()).getInventory();

        event.setSignLine(QUANTITY_LINE, StockCounter.getQuantityLineWithCounter(amount, itemTradedByShop, chestShopInventory));
    }
}
