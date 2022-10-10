package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public class TransactionMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onCurrencyTransfer(CurrencyTransferEvent event) {
        if (event.getTransactionEvent() == null) {
            return;
        }
        if (event.getTransactionEvent().getTransactionType() == TransactionEvent.TransactionType.BUY) {
            sendBuyMessage(event);
        } else {
            sendSellMessage(event);
        }
    }

    protected static void sendBuyMessage(CurrencyTransferEvent event) {
        TransactionEvent transactionEvent = event.getTransactionEvent();
        Player player = transactionEvent.getClient();

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            sendMessage(player, transactionEvent.getClient().getName(), Messages.YOU_BOUGHT_FROM_SHOP, event, MessageTarget.BUYER, "owner", transactionEvent.getOwnerAccount().getName());
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(transactionEvent.getOwnerAccount().getUuid())) {
            Player owner = Bukkit.getPlayer(transactionEvent.getOwnerAccount().getUuid());
            sendMessage(owner, transactionEvent.getOwnerAccount().getName(), Messages.SOMEBODY_BOUGHT_FROM_YOUR_SHOP, event, MessageTarget.SELLER, "buyer", player.getName());
        }
    }

    protected static void sendSellMessage(CurrencyTransferEvent event) {
        TransactionEvent transactionEvent = event.getTransactionEvent();
        Player player = transactionEvent.getClient();

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            sendMessage(player, transactionEvent.getClient().getName(), Messages.YOU_SOLD_TO_SHOP, event, MessageTarget.SELLER, "buyer", transactionEvent.getOwnerAccount().getName());
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(transactionEvent.getOwnerAccount().getUuid())) {
            Player owner = Bukkit.getPlayer(transactionEvent.getOwnerAccount().getUuid());
            sendMessage(owner, transactionEvent.getOwnerAccount().getName(), Messages.SOMEBODY_SOLD_TO_YOUR_SHOP, event, MessageTarget.BUYER, "seller", player.getName());
        }
    }

    private static void sendMessage(Player player, String playerName, Messages.Message rawMessage, CurrencyTransferEvent event, MessageTarget messageTarget, String... replacements) {
        TransactionEvent transactionEvent = event.getTransactionEvent();

        BigDecimal actualAmount = getTransactionActualAmount(event, messageTarget);

        Location loc = transactionEvent.getSign().getLocation();
        Map<String, String> replacementMap = new LinkedHashMap<>();
        replacementMap.put("price", Economy.formatBalance(actualAmount));
        replacementMap.put("world", loc.getWorld().getName());
        replacementMap.put("x", String.valueOf(loc.getBlockX()));
        replacementMap.put("y", String.valueOf(loc.getBlockY()));
        replacementMap.put("z", String.valueOf(loc.getBlockZ()));
        replacementMap.put("material", "%item");

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            replacementMap.put(replacements[i], replacements[i + 1]);
        }

        if (Properties.SHOWITEM_MESSAGE && MaterialUtil.Show.sendMessage(player, playerName, rawMessage, transactionEvent.getStock(), replacementMap)) {
            return;
        }

        if (player != null) {
            replacementMap.put("item", ItemUtil.getItemList(transactionEvent.getStock()));
            rawMessage.sendWithPrefix(player, replacementMap);
        } else if (playerName != null) {
            replacementMap.put("item", ItemUtil.getItemList(transactionEvent.getStock()));
            ChestShop.sendBungeeMessage(playerName, rawMessage, replacementMap);
        }
    }

    private static BigDecimal getTransactionActualAmount(CurrencyTransferEvent event, MessageTarget messageTarget) {
        if (messageTarget == MessageTarget.SELLER) {
            return event.getAmountReceived();
        } else {
            return event.getAmountSent();
        }
    }

    private enum MessageTarget {
        BUYER,
        SELLER
    }

}
