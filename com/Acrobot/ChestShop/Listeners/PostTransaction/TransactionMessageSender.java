package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

import static com.Acrobot.ChestShop.Config.Language.*;
import static com.Acrobot.ChestShop.Config.Property.SHOW_TRANSACTION_INFORMATION_CLIENT;
import static com.Acrobot.ChestShop.Config.Property.SHOW_TRANSACTION_INFORMATION_OWNER;

/**
 * @author Acrobot
 */
public class TransactionMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
            sendBuyMessage(event);
        } else {
            sendSellMessage(event);
        }
    }

    protected static void sendBuyMessage(TransactionEvent event) {
        String itemName = parseItemInformation(event.getStock());
        String owner = event.getOwner().getName();

        Player player = event.getClient();

        String price = Economy.formatBalance(event.getPrice());

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            String message = formatMessage(YOU_BOUGHT_FROM_SHOP, itemName, price);
            message = message.replace("%owner", owner);

            player.sendMessage(message);
        }

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_OWNER)) {
            String message = formatMessage(SOMEBODY_BOUGHT_FROM_YOUR_SHOP, itemName, price);
            message = message.replace("%buyer", player.getName());

            sendMessageToOwner(message, event);
        }
    }

    protected static void sendSellMessage(TransactionEvent event) {
        String itemName = parseItemInformation(event.getStock());
        String owner = event.getOwner().getName();

        Player player = event.getClient();

        String price = Economy.formatBalance(event.getPrice());

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            String message = formatMessage(YOU_SOLD_TO_SHOP, itemName, price);
            message = message.replace("%buyer", owner);

            player.sendMessage(message);
        }

        if (Config.getBoolean(SHOW_TRANSACTION_INFORMATION_OWNER)) {
            String message = formatMessage(SOMEBODY_SOLD_TO_YOUR_SHOP, itemName, price);
            message = message.replace("%seller", player.getName());

            sendMessageToOwner(message, event);
        }
    }

    private static String parseItemInformation(ItemStack[] items) {
        List<ItemStack> stock = new LinkedList<ItemStack>();

        for (ItemStack item : items) {
            boolean added = false;

            for (ItemStack iStack : stock) {
                if (MaterialUtil.equals(item, iStack)) {
                    iStack.setAmount(iStack.getAmount() + item.getAmount());
                    added = true;
                    break;
                }
            }

            if (!added) {
                stock.add(item);
            }
        }

        StringBuilder message = new StringBuilder(15);

        for (ItemStack item : stock) {
            message.append(item.getAmount()).append(' ').append(item.getType().name());
        }

        return message.toString();
    }

    private static void sendMessageToOwner(String message, TransactionEvent event) {
        String owner = event.getOwner().getName();

        Player player = Bukkit.getPlayer(owner);

        if (player != null) {
            player.sendMessage(message);
        }
    }

    private static String formatMessage(Language message, String item, String price) {
        return Config.getLocal(message)
                .replace("%item", item)
                .replace("%price", price);
    }
}
