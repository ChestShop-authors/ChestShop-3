package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ErrorMessageSender implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        Messages.Message message = null;

        switch (event.getOutcome()) {
            case UNKNOWN_PLAYER:
                message = Messages.PLAYER_NOT_FOUND;
                break;
            case INVALID_ITEM:
                message = Messages.INCORRECT_ITEM_ID;
                break;
            case INVALID_PRICE:
                message = Messages.INVALID_SHOP_PRICE;
                break;
            case INVALID_QUANTITY:
                message = Messages.INVALID_SHOP_QUANTITY;
                break;
            case SELL_PRICE_HIGHER_THAN_BUY_PRICE:
                message = Messages.SELL_PRICE_HIGHER_THAN_BUY_PRICE;
                break;
            case NO_CHEST:
                message = Messages.NO_CHEST_DETECTED;
                break;
            case NO_PERMISSION:
                message = Messages.NO_PERMISSION;
                break;
            case NO_PERMISSION_FOR_TERRAIN:
                message = Messages.CANNOT_CREATE_SHOP_HERE;
                break;
            case NO_PERMISSION_FOR_CHEST:
                message = Messages.CANNOT_ACCESS_THE_CHEST;
                break;
            case NOT_ENOUGH_MONEY:
                message = Messages.NOT_ENOUGH_MONEY;
                break;
            case ITEM_AUTOFILL:
                message = Messages.CLICK_TO_AUTOFILL_ITEM;
                break;
            default:
                break;
        }

        if (message != null) {
            message.sendWithPrefix(event.getPlayer());
        }
    }
}
