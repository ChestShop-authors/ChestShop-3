package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.MaterialUtil.getSignName;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;

/**
 * @author Acrobot
 */
public class TransactionLogger implements Listener {
    private static final String BUY_MESSAGE = "%1$s bought %2$s for %3$.2f from %4$s at %5$s";
    private static final String SELL_MESSAGE = "%1$s sold %2$s for %3$.2f to %4$s at %5$s";

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTransaction(final TransactionEvent event) {
        ChestShop.getBukkitServer().getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override public void run() {
                String template = (event.getTransactionType() == BUY ? BUY_MESSAGE : SELL_MESSAGE);

                StringBuilder items = new StringBuilder(50);

                for (ItemStack item : event.getStock()) {
                    items.append(item.getAmount()).append(' ').append(getSignName(item));
                }

                String message = String.format(template,
                        event.getClient().getName(),
                        items.toString(),
                        event.getPrice(),
                        NameManager.getUsername(event.getOwner().getUniqueId()),
                        LocationUtil.locationToString(event.getSign().getLocation()));

                ChestShop.getBukkitLogger().info(message);
            }
        });
    }

    @EventHandler
    public static void onTransactionLogToDB(TransactionEvent event) {
        if (!Properties.LOG_TO_DATABASE && !Properties.GENERATE_STATISTICS_PAGE) {
            return;
        }

        double pricePerStack = event.getPrice() / event.getStock().length;

        for (ItemStack item : event.getStock()) {
            Transaction transaction = new Transaction();

            transaction.setAmount(item.getAmount());

            transaction.setItemID(item.getTypeId());
            transaction.setItemDurability(item.getDurability());

            transaction.setPrice((float) pricePerStack);

            transaction.setShopOwner(NameManager.getUsername(event.getOwner().getUniqueId()));
            transaction.setShopUser(NameManager.getUsername(event.getClient().getUniqueId()));

            transaction.setSec(System.currentTimeMillis() / 1000);
            transaction.setBuy(event.getTransactionType() == BUY);

            Queue.addToQueue(transaction);
        }
    }
}
