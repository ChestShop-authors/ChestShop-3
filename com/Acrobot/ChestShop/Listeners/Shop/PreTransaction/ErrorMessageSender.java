package com.Acrobot.ChestShop.Listeners.Shop.PreTransaction;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ErrorMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onMessage(PreTransactionEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        Language message = null;

        switch(event.getTransactionOutcome()) {
            case SHOP_DOES_NOT_BUY_THIS_ITEM:
                message = Language.NO_BUYING_HERE;
                break;
            case SHOP_DOES_NOT_SELL_THIS_ITEM:
                message = Language.NO_SELLING_HERE;
                break;
            case CLIENT_DOES_NOT_HAVE_PERMISSION:
                message = Language.NO_PERMISSION;
                break;
            case CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Language.NOT_ENOUGH_MONEY;
                break;
            case SHOP_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Language.NOT_ENOUGH_MONEY_SHOP;
                break;
            case NOT_ENOUGH_SPACE_IN_CHEST:
                message = Language.NOT_ENOUGH_SPACE_IN_CHEST;
                break;
            case NOT_ENOUGH_SPACE_IN_INVENTORY:
                message = Language.NOT_ENOUGH_SPACE_IN_INVENTORY;
                break;
            case NOT_ENOUGH_STOCK_IN_INVENTORY:
                message = Language.NOT_ENOUGH_ITEMS_TO_SELL;
                break;
            case NOT_ENOUGH_STOCK_IN_CHEST:
                sendMessageToOwner(event.getOwner(), Language.NOT_ENOUGH_STOCK_IN_YOUR_SHOP);
                message = Language.NOT_ENOUGH_STOCK;
                break;
            case SHOP_IS_RESTRICTED:
                message = Language.ACCESS_DENIED;
                break;
            case INVALID_SHOP:
                message = Language.INVALID_SHOP_DETECTED;
                break;
        }

        if (message != null) {
            event.getClient().sendMessage(Config.getLocal(message));
        }
    }

    private static void sendMessageToOwner(OfflinePlayer owner, Language message) {
        if (owner.isOnline() && Config.getBoolean(Property.SHOW_MESSAGE_OUT_OF_STOCK)) {
            Player player = (Player) owner;
            player.sendMessage(Config.getLocal(message));
        }
    }
}
