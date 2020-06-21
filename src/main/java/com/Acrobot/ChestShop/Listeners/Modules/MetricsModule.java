package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class MetricsModule implements Listener {

    private static final long RESET_MINUTES = 30;

    private static long lastReset = System.currentTimeMillis();

    private static int buyTransactionsLast = -1;
    private static int sellTransactionsLast = -1;
    private static long buyTransactionsCurrent = 0;
    private static long sellTransactionsCurrent = 0;

    private static int boughtItemsLast = -1;
    private static int soldItemsLast = -1;
    private static long boughtItemsCurrent = 0;
    private static long soldItemsCurrent = 0;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onTransaction(final TransactionEvent event) {
        checkReset();
        switch (event.getTransactionType()) {
            case BUY:
                buyTransactionsCurrent++;
                for (ItemStack itemStack : event.getStock()) {
                    boughtItemsCurrent += itemStack.getAmount();
                }
                break;
            case SELL:
                sellTransactionsCurrent++;
                for (ItemStack itemStack : event.getStock()) {
                    soldItemsCurrent += itemStack.getAmount();
                }
                break;
        }
    }

    public static int getBuyTransactions() {
        checkReset();
        return buyTransactionsLast > -1 ? buyTransactionsLast : NumberUtil.toInt(buyTransactionsCurrent);
    }

    public static int getSellTransactions() {
        checkReset();
        return sellTransactionsLast > -1 ? sellTransactionsLast : NumberUtil.toInt(sellTransactionsCurrent);
    }

    public static int getTotalTransactions() {
        checkReset();
        return getBuyTransactions() + getSellTransactions();
    }

    public static int getBoughtItemsCount() {
        checkReset();
        return boughtItemsLast > -1 ? boughtItemsLast : NumberUtil.toInt(boughtItemsCurrent);
    }

    public static int getSoldItemsCount() {
        checkReset();
        return soldItemsLast > -1 ? soldItemsLast : NumberUtil.toInt(soldItemsCurrent);
    }

    public static int getTotalItemsCount() {
        checkReset();
        return getBoughtItemsCount() + getSoldItemsCount();
    }

    private static void checkReset() {
        if (lastReset + RESET_MINUTES * 60 * 1000 < System.currentTimeMillis()) {
            lastReset = System.currentTimeMillis();
            buyTransactionsLast = NumberUtil.toInt(buyTransactionsCurrent);
            buyTransactionsCurrent = 0;
            sellTransactionsLast = NumberUtil.toInt(sellTransactionsCurrent);
            sellTransactionsCurrent = 0;
            
            boughtItemsLast = NumberUtil.toInt(boughtItemsCurrent);
            boughtItemsCurrent = 0;
            soldItemsLast = NumberUtil.toInt(soldItemsCurrent);
            soldItemsCurrent = 0;
        }
    }
}
