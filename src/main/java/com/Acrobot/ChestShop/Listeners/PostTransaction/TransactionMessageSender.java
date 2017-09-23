package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        String owner = NameManager.getUsername(event.getOwner().getUniqueId());

        Player player = event.getClient();

        String price = Economy.formatBalance(event.getPrice());

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            String message = formatMessage(Messages.YOU_BOUGHT_FROM_SHOP, itemName, price);
            message = message.replace("%owner", owner);

            player.sendMessage(message);
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwner())) {
            String message = formatMessage(Messages.SOMEBODY_BOUGHT_FROM_YOUR_SHOP, itemName, price);
            message = message.replace("%buyer", player.getName());

            sendMessageToOwner(message, event);
        }
    }

    protected static void sendSellMessage(TransactionEvent event) {
        String itemName = parseItemInformation(event.getStock());
        String owner = NameManager.getUsername(event.getOwner().getUniqueId());

        Player player = event.getClient();

        String price = Economy.formatBalance(event.getPrice());

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            String message = formatMessage(Messages.YOU_SOLD_TO_SHOP, itemName, price);
            message = message.replace("%buyer", owner);

            player.sendMessage(message);
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwner())) {
            String message = formatMessage(Messages.SOMEBODY_SOLD_TO_YOUR_SHOP, itemName, price);
            message = message.replace("%seller", player.getName());

            sendMessageToOwner(message, event);
        }
    }

    private static String parseItemInformation(ItemStack[] items) {
        ItemStack[] stock = InventoryUtil.mergeSimilarStacks(items);

        List<String> itemText = new ArrayList<>();

        for (ItemStack item : stock) {
            itemText.add(item.getAmount() + " " + MaterialUtil.getName(item));
        }

        return StringUtil.joinArray(itemText);
    }

    private static void sendMessageToOwner(String message, TransactionEvent event) {
        UUID owner = event.getOwner().getUniqueId();

        Player player = Bukkit.getPlayer(owner);

        if (player != null) {
            player.sendMessage(message);
        }
    }

    private static String formatMessage(String message, String item, String price) {
        return Messages.prefix(message)
                .replace("%item", item)
                .replace("%price", price);
    }
}
