package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;

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

        String metadata = getMetadata(itemCode);
        String longName = MaterialUtil.getName(item);

        if (longName.length() <= (MAXIMUM_SIGN_LETTERS - metadata.length())) {
            if (isSameItem(longName + metadata, item)) {
                String itemName = StringUtil.capitalizeFirstLetter(longName);

                event.setSignLine(ITEM_LINE, itemName + metadata);
                return;
            }
        }

        String code = MaterialUtil.getName(item, SHORT_NAME);

        String[] parts = itemCode.split("(?=:|-|#)", 2);
        String data = (parts.length > 1 ? parts[1] : "");

        if (!data.isEmpty() && code.length() > (MAXIMUM_SIGN_LETTERS - data.length())) {
            code = code.substring(0, MAXIMUM_SIGN_LETTERS - data.length());
        }

        if (!isSameItem(code + data, item)) {
            code = String.valueOf(item.getTypeId());
        }

        code = StringUtil.capitalizeFirstLetter(code);

        event.setSignLine(ITEM_LINE, code + data);
    }

    private static boolean isSameItem(String newCode, ItemStack item) {
        ItemStack newItem = MaterialUtil.getItem(newCode);

        return newItem != null && MaterialUtil.equals(newItem, item);
    }

    private static String getMetadata(String itemCode) {
        Matcher m = METADATA.matcher(itemCode);

        if (!m.find()) {
            return "";
        }

        return m.group();
    }
}
