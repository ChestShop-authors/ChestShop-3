package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NO_PERMISSION;
import static com.Acrobot.ChestShop.Permission.*;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static org.bukkit.event.EventPriority.HIGH;

/**
 * @author Acrobot
 */
public class PermissionChecker implements Listener {

    @EventHandler(priority = HIGH)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        Player player = event.getPlayer();

        String priceLine = event.getSignLine(PRICE_LINE);
        String itemLine = event.getSignLine(ITEM_LINE);

        ItemParseEvent parseEvent = new ItemParseEvent(itemLine);
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack item = parseEvent.getItem();

        if (item == null) {
            if (!Permission.has(player, SHOP_CREATION)) {
                event.setOutcome(NO_PERMISSION);
            }
            return;
        }

        String matID = item.getType().toString().toLowerCase(Locale.ROOT);
        if (PriceUtil.hasBuyPrice(priceLine)) {
            if (Permission.has(player, SHOP_CREATION_BUY_ID + matID)) {
                return;
            }
            if (Permission.has(player, SHOP_CREATION) || (Permission.has(player, SHOP_CREATION_ID + matID) && Permission.has(player, SHOP_CREATION_BUY))) {
                return;
            }
            event.setOutcome(NO_PERMISSION);
            return;
        }

        if (PriceUtil.hasSellPrice(priceLine)) {
            if (Permission.has(player, SHOP_CREATION_SELL_ID + matID)) {
                return;
            }
            if (Permission.has(player, SHOP_CREATION) || (Permission.has(player, SHOP_CREATION_ID + matID) && Permission.has(player, SHOP_CREATION_SELL))) {
                return;
            }
            event.setOutcome(NO_PERMISSION);
            return;
        }
    }
}
