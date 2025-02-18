package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_QUANTITY;

/**
 * @author Acrobot
 */
public class QuantityChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        int amount = -1;
        try {
            amount = ChestShopSign.getQuantity(event.getSignLines());
        } catch (NumberFormatException ignored) {} // not a quantity on the line

        if (amount < 1 || amount > Properties.MAX_SHOP_AMOUNT) {
            event.setOutcome(INVALID_QUANTITY);
        }
    }
}
