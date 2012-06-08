package com.Acrobot.ChestShop.Listeners.Transaction;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.MaterialUtil.getSignName;
import static com.Acrobot.ChestShop.Config.Property.GENERATE_STATISTICS_PAGE;
import static com.Acrobot.ChestShop.Config.Property.LOG_TO_DATABASE;
import static com.Acrobot.ChestShop.Events.TransactionEvent.Type.BUY;

/**
 * @author Acrobot
 */
public class TransactionLogger implements Listener {
    @EventHandler
    public static void onTransaction(TransactionEvent event) {
        StringBuilder message = new StringBuilder(70);

        message.append(event.getClient().getName());

        if (event.getTransactionType() == BUY) {
            message.append(" bought ");
        } else {
            message.append(" sold ");
        }

        message.append(event.getItemAmount()).append(' ');
        message.append(getSignName(event.getItem())).append(" for ");
        message.append(event.getPrice());

        if (event.getTransactionType() == BUY) {
            message.append(" from ");
        } else {
            message.append(" to ");
        }

        message.append(event.getOwner()).append(' ');

        message.append(locationToString(event.getSign().getLocation()));

        ChestShop.getBukkitLogger().info(message.toString());
    }

    @EventHandler
    public static void onTransactionLogToDB(TransactionEvent event) {
        if (!Config.getBoolean(LOG_TO_DATABASE) && !Config.getBoolean(GENERATE_STATISTICS_PAGE)) {
            return;
        }

        Transaction transaction = new Transaction();
        ItemStack item = event.getItem();

        transaction.setAmount(event.getItemAmount());

        transaction.setItemID(item.getTypeId());
        transaction.setItemDurability(item.getDurability());

        transaction.setPrice((float) event.getPrice());

        transaction.setShopOwner(event.getOwner());
        transaction.setShopUser(event.getClient().getName());

        transaction.setSec(System.currentTimeMillis() / 1000);
        transaction.setBuy(event.getTransactionType() == BUY);

        Queue.addToQueue(transaction);
    }

    private static String locationToString(Location loc) {
        return '[' + loc.getWorld().getName() + "] " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }
}
