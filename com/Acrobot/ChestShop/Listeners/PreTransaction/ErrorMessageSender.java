package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

import static com.Acrobot.ChestShop.Configuration.Messages.*;

/**
 * @author Acrobot
 */
public class ErrorMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onMessage(PreTransactionEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        String message = null;

        switch (event.getTransactionOutcome()) {
            case SHOP_DOES_NOT_BUY_THIS_ITEM:
                message = Messages.NO_BUYING_HERE;
                break;
            case SHOP_DOES_NOT_SELL_THIS_ITEM:
                message = Messages.NO_SELLING_HERE;
                break;
            case CLIENT_DOES_NOT_HAVE_PERMISSION:
                message = Messages.NO_PERMISSION;
                break;
            case CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Messages.NOT_ENOUGH_MONEY;
                break;
            case SHOP_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Messages.NOT_ENOUGH_MONEY_SHOP;
                break;
            case NOT_ENOUGH_SPACE_IN_CHEST:
                message = Messages.NOT_ENOUGH_SPACE_IN_CHEST;
                break;
            case NOT_ENOUGH_SPACE_IN_INVENTORY:
                message = Messages.NOT_ENOUGH_SPACE_IN_INVENTORY;
                break;
            case NOT_ENOUGH_STOCK_IN_INVENTORY:
                message = Messages.NOT_ENOUGH_ITEMS_TO_SELL;
                break;
            case NOT_ENOUGH_STOCK_IN_CHEST:
                String messageOutOfStock = Messages.prefix(NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", getItemNames(event.getStock()));
                sendMessageToOwner(event.getOwner(), messageOutOfStock);
                message = Messages.NOT_ENOUGH_STOCK;
                break;
            case SHOP_DEPOSIT_FAILED:
            	String messageDepositFailed = Messages.prefix(DEPOSIT_FAILED);
                sendMessageToOwner(event.getOwner(), messageDepositFailed);
                message = DEPOSIT_FAILED;
                break;
            case SHOP_IS_RESTRICTED:
                message = Messages.ACCESS_DENIED;
                break;
            case INVALID_SHOP:
                message = Messages.INVALID_SHOP_DETECTED;
                break;
			case CREATIVE_MODE_PROTECTION:
				break;
			case OTHER:
				break;
			case SPAM_CLICKING_PROTECTION:
				break;
			case TRANSACTION_SUCCESFUL:
				break;
			default:
				break;
        }

        if (message != null) {
            event.getClient().sendMessage(Messages.prefix(message));
        }
    }

    private static String getItemNames(ItemStack[] stock) {
        List<ItemStack> items = new LinkedList<ItemStack>();

        for (ItemStack stack : stock) {
            boolean hadItem = false;

            for (ItemStack item : items) {
                if (MaterialUtil.equals(stack, item)) {
                    item.setAmount(item.getAmount() + stack.getAmount());
                    hadItem = true;
                }
            }

            if (!hadItem) {
                items.add(stack.clone());
            }
        }

        StringBuilder names = new StringBuilder(50);

        for (ItemStack item : items) {
            names.append(MaterialUtil.getName(item)).append(',').append(' ');
        }

        return names.toString();
    }

    private static void sendMessageToOwner(OfflinePlayer owner, String message) {
        if (owner.isOnline() && Properties.SHOW_MESSAGE_OUT_OF_STOCK) {
            Player player = (Player) owner;
            player.sendMessage(message);
        }
    }
}
