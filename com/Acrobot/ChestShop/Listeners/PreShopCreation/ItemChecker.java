package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.MaterialUtil.*;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {
    private static final short MAXIMUM_SIGN_LETTERS = 15;

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String itemCode = event.getSignLine(ITEM_LINE);
        ItemStack item = MaterialUtil.getItem(itemCode);

        if (item == null) {
            event.setOutcome(INVALID_ITEM);
            return;
        }

        if (Odd.getFromString(itemCode) != null) {
            return; // The OddItem name is OK
        }

        String enchantment = getEnchantment(itemCode);
        String longName = MaterialUtil.getName(item, LONG_NAME);

        if (longName.length() <= (MAXIMUM_SIGN_LETTERS - enchantment.length())) {
            if (isStillValidItem(longName + enchantment, item)) {
                String itemName = StringUtil.capitalizeFirstLetter(longName + enchantment);

                event.setSignLine(ITEM_LINE, itemName);
                return;
            }
        }

        String code = MaterialUtil.getName(item, SHORT_NAME);

        String[] parts = itemCode.split("(?=:|-)", 2);
        String data = (parts.length > 1 ? parts[1] : "");

        if (code.length() > (MAXIMUM_SIGN_LETTERS - 1 - data.length())) {
            code = code.substring(0, MAXIMUM_SIGN_LETTERS - 1 - data.length()) + data;
        }

        if (!isStillValidItem(code, item)) {
            code = String.valueOf(item.getTypeId()) + data;
        }

        code = StringUtil.capitalizeFirstLetter(code);

        event.setSignLine(ITEM_LINE, code);
    }

    private static boolean isStillValidItem(String newCode, ItemStack item) {
        ItemStack newItem = MaterialUtil.getItem(newCode);

        return newItem != null && MaterialUtil.equals(newItem, item);
    }

    private static String getEnchantment(String itemCode) {
        if (!ENCHANTMENT.matcher(itemCode).matches()) {
            return "";
        }

        return '-' + ENCHANTMENT.matcher(itemCode).group();
    }
}
