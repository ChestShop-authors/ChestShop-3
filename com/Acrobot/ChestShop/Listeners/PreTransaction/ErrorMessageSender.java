package com.Acrobot.ChestShop.Listeners.PreTransaction;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Acrobot
 */
public class ErrorMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onMessage(PreTransactionEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        Language message = null;

        switch (event.getTransactionOutcome()) {
            case SHOP_DOES_NOT_BUY_THIS_ITEM:
                message = Language.NO_BUYING_HERE;
                break;
            case SHOP_DOES_NOT_SELL_THIS_ITEM:
                message = Language.NO_SELLING_HERE;
                break;
            case CLIENT_DOES_NOT_HAVE_PERMISSION:
                message = Language.NO_PERMISSION;
                break;
            case CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Language.NOT_ENOUGH_MONEY;
                break;
            case SHOP_DOES_NOT_HAVE_ENOUGH_MONEY:
                message = Language.NOT_ENOUGH_MONEY_SHOP;
                break;
            case NOT_ENOUGH_SPACE_IN_CHEST:
                message = Language.NOT_ENOUGH_SPACE_IN_CHEST;
                break;
            case NOT_ENOUGH_SPACE_IN_INVENTORY:
                message = Language.NOT_ENOUGH_SPACE_IN_INVENTORY;
                break;
            case NOT_ENOUGH_STOCK_IN_INVENTORY:
                message = Language.NOT_ENOUGH_ITEMS_TO_SELL;
                break;
            case NOT_ENOUGH_STOCK_IN_CHEST:
                String messageOutOfStock = Config.getLocal(Language.NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", getItemNames(event.getStock()));
                sendMessageToOwner(event.getOwner(), messageOutOfStock);
                message = Language.NOT_ENOUGH_STOCK;
                break;
            case SHOP_IS_RESTRICTED:
                message = Language.ACCESS_DENIED;
                break;
            case INVALID_SHOP:
                message = Language.INVALID_SHOP_DETECTED;
                break;
        }

        if (message != null) {
            event.getClient().sendMessage(Config.getLocal(message));
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
        if (owner.isOnline() && Config.getBoolean(Property.SHOW_MESSAGE_OUT_OF_STOCK)) {
            Player player = (Player) owner;
            player.sendMessage(message);
        }
    }
}
