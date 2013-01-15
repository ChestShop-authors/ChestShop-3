package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.Breeze.Utils.PriceUtil.isPrice;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_PRICE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;

/**
 * @author Acrobot
 */
public class PriceChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String line = event.getSignLine(PRICE_LINE).toUpperCase();
        String[] part = line.split(":");

        if (isPrice(part[0])) {
            line = "B " + line;
        }

        if (part.length > 1 && isPrice(part[1])) {
            line += " S";
        }

        line = line.replace('b', 'B').replace('s', 'S');

        if (line.length() > 15) {
            line = line.replace(" ", "");
        }

        if (line.length() > 15) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        event.setSignLine(PRICE_LINE, line);

        double buyPrice = PriceUtil.getBuyPrice(line);
        double sellPrice = PriceUtil.getSellPrice(line);

        if (buyPrice == 0 && sellPrice == 0) {
            event.setOutcome(INVALID_PRICE);
        }
    }
}
