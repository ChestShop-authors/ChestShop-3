package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class TransactionMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
            sendBuyMessage(event);
        } else {
            sendSellMessage(event);
        }
    }

    protected static void sendBuyMessage(TransactionEvent event) {
        Player player = event.getClient();

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            sendMessage(player, event.getClient().getName(), Messages.YOU_BOUGHT_FROM_SHOP, event, "owner", event.getOwnerAccount().getName());
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwnerAccount().getUuid())) {
            Player owner = Bukkit.getPlayer(event.getOwnerAccount().getUuid());
            sendMessage(owner, event.getOwnerAccount().getName(), Messages.SOMEBODY_BOUGHT_FROM_YOUR_SHOP, event, "buyer", player.getName());
        }
    }
    
    protected static void sendSellMessage(TransactionEvent event) {
        Player player = event.getClient();

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            sendMessage(player, event.getClient().getName(), Messages.YOU_SOLD_TO_SHOP, event, "buyer", event.getOwnerAccount().getName());
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwnerAccount().getUuid())) {
            Player owner = Bukkit.getPlayer(event.getOwnerAccount().getUuid());
            sendMessage(owner, event.getOwnerAccount().getName(), Messages.SOMEBODY_SOLD_TO_YOUR_SHOP, event, "seller", player.getName());
        }
    }
    
    private static void sendMessage(Player player, String playerName, String rawMessage, TransactionEvent event, String... replacements) {
        Location loc = event.getSign().getLocation();
        String message = Messages.prefix(rawMessage)
                .replace("%price", Economy.formatBalance(event.getExactPrice()))
                .replace("%world", loc.getWorld().getName())
                .replace("%x", String.valueOf(loc.getBlockX()))
                .replace("%y", String.valueOf(loc.getBlockY()))
                .replace("%z", String.valueOf(loc.getBlockZ()));
        
        for (int i = 0; i + 1 < replacements.length; i+=2) {
            message = message.replace("%" + replacements[i], replacements[i + 1]);
        }

        if (player != null) {
            if (Properties.SHOWITEM_MESSAGE && MaterialUtil.Show.sendMessage(player, message, event.getStock())) {
                return;
            }
            player.sendMessage(message.replace("%item", MaterialUtil.getItemList(event.getStock())));
        } else if (playerName != null) {
            ChestShop.sendBungeeMessage(playerName, message.replace("%item", MaterialUtil.getItemList(event.getStock())));
        }
    }

}
