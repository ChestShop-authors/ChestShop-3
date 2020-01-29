package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Locale;

import static com.Acrobot.Breeze.Utils.PriceUtil.isPrice;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_PRICE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;

/**
 * @author Acrobot
 */
public class PriceChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String line = event.getSignLine(PRICE_LINE).toUpperCase(Locale.ROOT);
        if (Properties.PRICE_PRECISION <= 0) {
            line = line.replaceAll("\\.\\d*", ""); //remove too many decimal places
        } else {
            line = line.replaceAll("(\\.\\d{0," + Properties.PRICE_PRECISION + "})\\d*", "$1"); //remove too many decimal places
        }
        line = line.replaceAll("(\\.\\d*[1-9])0+", "$1"); //remove trailing zeroes
        line = line.replaceAll("(\\d)\\.0+(\\D|$)", "$1$2"); //remove point and zeroes from strings that only have trailing zeros

        String[] part = line.split(":");

        if (part.length > 1 && (isInvalid(part[0]) ^ isInvalid(part[1]))) {
            line = line.replace(':', ' ');
            part = new String[]{line};
        }

        if (part[0].split(" ").length > 2) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (line.indexOf('B') != line.lastIndexOf('B') || line.indexOf('S') != line.lastIndexOf('S')) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (isPrice(part[0])) {
            line = "B " + line;
        }

        if (part.length > 1 && isPrice(part[1])) {
            line += " S";
        }

        if (line.length() > 15) {
            line = line.replace(" ", "");
        }

        if (line.length() > 15) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        event.setSignLine(PRICE_LINE, line);

        if (!PriceUtil.hasBuyPrice(line) && !PriceUtil.hasSellPrice(line)) {
            event.setOutcome(INVALID_PRICE);
        }
    }

    private static boolean isInvalid(String part) {
        char characters[] = {'B', 'S'};

        for (char character : characters) {
            if (part.contains(Character.toString(character))) {
                return !PriceUtil.hasPrice(part, character);
            }
        }

        return false;
    }
}
