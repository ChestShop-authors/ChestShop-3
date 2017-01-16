package com.Acrobot.ChestShop.Listeners.ShopRemoval;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.UUID;

import static com.Acrobot.ChestShop.Permission.NOFEE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class ShopRefundListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopDestroy(ShopDestroyedEvent event) {
        double refundPrice = Properties.SHOP_REFUND_PRICE;

        if (event.getDestroyer() == null || Permission.has(event.getDestroyer(), NOFEE) || refundPrice == 0) {
            return;
        }

        String ownerName = NameManager.getFullUsername(event.getSign().getLine(NAME_LINE));
        if (ownerName.isEmpty()) {
            return;
        }

        UUID owner = NameManager.getUUID(ownerName);

        CurrencyAddEvent currencyEvent = new CurrencyAddEvent(BigDecimal.valueOf(refundPrice), owner, event.getSign().getWorld());
        ChestShop.callEvent(currencyEvent);

        if (!Economy.getServerAccountName().isEmpty()) {
            CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(
                    BigDecimal.valueOf(refundPrice),
                    NameManager.getUUID(Economy.getServerAccountName()),
                    event.getSign().getWorld());
            ChestShop.callEvent(currencySubtractEvent);
        }

        String message = Messages.SHOP_REFUNDED.replace("%amount", Economy.formatBalance(refundPrice));
        event.getDestroyer().sendMessage(Messages.prefix(message));
    }
}
