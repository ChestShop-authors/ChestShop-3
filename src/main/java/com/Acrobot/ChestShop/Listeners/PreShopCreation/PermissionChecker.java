package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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

        if (Permission.has(player, ADMIN)) {
            return;
        }

        String priceLine = event.getSignLine(PRICE_LINE);
        String itemLine = event.getSignLine(ITEM_LINE);

        ItemStack item = MaterialUtil.getItem(itemLine);

        if (item == null || Permission.has(player, SHOP_CREATION_ID + item.getType().toString().toLowerCase())) {
            return;
        }

        if (PriceUtil.hasBuyPrice(priceLine) && !Permission.has(player, SHOP_CREATION_BUY)) {
            event.setOutcome(NO_PERMISSION);
            return;
        }

        if (PriceUtil.hasSellPrice(priceLine) && !Permission.has(player, SHOP_CREATION_SELL)) {
            event.setOutcome(NO_PERMISSION);
        }
    }
}
