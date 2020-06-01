package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.CLIENT_DOES_NOT_HAVE_PERMISSION;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;

/**
 * @author Acrobot
 */
public class PermissionChecker implements Listener {
    @EventHandler
    public static void onPermissionCheck(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player client = event.getClient();
        TransactionEvent.TransactionType transactionType = event.getTransactionType();

        String itemLine = event.getSign().getLine(ITEM_LINE);
        if (itemLine.contains("#") && Permission.hasPermissionSetFalse(client, (transactionType == BUY ? Permission.BUY_ID : Permission.SELL_ID) + itemLine)) {
            event.setCancelled(CLIENT_DOES_NOT_HAVE_PERMISSION);
            return;
        }

        for (ItemStack stock : event.getStock()) {
            String matID = stock.getType().toString().toLowerCase(Locale.ROOT);

            boolean hasPerm;

            if (transactionType == BUY) {
                hasPerm = Permission.has(client, Permission.BUY) || Permission.has(client, Permission.BUY_ID + matID);
            } else {
                hasPerm = Permission.has(client, Permission.SELL) || Permission.has(client, Permission.SELL_ID + matID);
            }

            if (!hasPerm) {
                event.setCancelled(CLIENT_DOES_NOT_HAVE_PERMISSION);
                return;
            }
        }
    }
}
