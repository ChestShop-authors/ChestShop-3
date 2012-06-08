package com.Acrobot.ChestShop.Listeners.Transaction;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Config.Language.*;
import static com.Acrobot.ChestShop.Config.Property.SHOW_TRANSACTION_INFORMATION_CLIENT;
import static com.Acrobot.ChestShop.Config.Property.SHOW_TRANSACTION_INFORMATION_OWNER;

/**
 * @author Acrobot
 */
public class TransactionMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() == TransactionEvent.Type.BUY) {
            sendBuyMessage(event);
        } else {
            sendSellMessage(event);
        }
    }

    protected static void sendBuyMessage(TransactionEvent event) {
        String itemName = StringUtil.capitalizeFirstLetter(event.getItem().getType().name());
        String owner = event.getOwner();

        Player player = event.getClient();

        int amount = event.getItemAmount();
        String price = Economy.formatBalance(event.getPrice());

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            String message = formatMessage(YOU_BOUGHT_FROM_SHOP, itemName, price, amount);
            message = message.replace("%owner", owner);

            player.sendMessage(message);
        }

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_OWNER)) {
            String message = formatMessage(SOMEBODY_BOUGHT_FROM_YOUR_SHOP, itemName, price, amount);
            message = message.replace("%buyer", player.getName());

            sendMessageToOwner(message, event);
        }
    }

    protected static void sendSellMessage(TransactionEvent event) {
        String itemName = StringUtil.capitalizeFirstLetter(event.getItem().getType().name());
        String owner = event.getOwner();

        Player player = event.getClient();

        int amount = event.getItemAmount();
        String price = Economy.formatBalance(event.getPrice());

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            String message = formatMessage(YOU_SOLD_TO_SHOP, itemName, price, amount);
            message = message.replace("%buyer", owner);

            player.sendMessage(message);
        }

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_OWNER)) {
            String message = formatMessage(SOMEBODY_SOLD_TO_YOUR_SHOP, itemName, price, amount);
            message = message.replace("%seller", player.getName());

            sendMessageToOwner(message, event);
        }
    }

    private static void sendMessageToOwner(String message, TransactionEvent event) {
        String owner = event.getOwner();

        Player player = Bukkit.getPlayer(owner);

        if (player != null) {
            player.sendMessage(message);
        }
    }

    private static String formatMessage(Language message, String item, String price, int amount) {
        return Config.getLocal(message)
                .replace("%amount", String.valueOf(amount))
                .replace("%item", item)
                .replace("%price", price);
    }
}
