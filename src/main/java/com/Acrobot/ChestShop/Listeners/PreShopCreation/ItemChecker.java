package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;

import static com.Acrobot.Breeze.Utils.MaterialUtil.*;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_ITEM;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.ITEM_AUTOFILL;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.AUTOFILL_CODE;

/**
 * @author Acrobot
 */
public class ItemChecker implements Listener {
    private static final short MAXIMUM_SIGN_LETTERS = 15;
    
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String itemCode = event.getSignLine(ITEM_LINE);
        ItemStack item = MaterialUtil.getItem(itemCode);

        if (Odd.getFromString(itemCode) != null) {
            return; // The OddItem name is OK
        }

        if (item == null) {
            if (Properties.ALLOW_AUTO_ITEM_FILL && itemCode.equals(AUTOFILL_CODE)) {
                boolean foundItem = false;
                Chest chest = uBlock.findConnectedChest(event.getSign());
                if (chest != null) {
                    for (ItemStack stack : chest.getInventory().getContents()) {
                        if (!MaterialUtil.isEmpty(stack)) {
                            item = stack;
                            itemCode = MaterialUtil.getSignName(stack);
            
                            event.setSignLine(ITEM_LINE, itemCode);
                            foundItem = true;
            
                            break;
                        }
                    }
                }
    
                if (!foundItem) {
                    event.setSignLine(ITEM_LINE, ChatColor.BOLD + ChestShopSign.AUTOFILL_CODE);
                    event.setOutcome(ITEM_AUTOFILL);
                    event.getPlayer().sendMessage(Messages.prefix(Messages.CLICK_TO_AUTOFILL_ITEM));
                    return;
                }
            } else {
                event.setOutcome(INVALID_ITEM);
                return;
            }
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

        if (code.length() > (MAXIMUM_SIGN_LETTERS - data.length())) {
            code = code.substring(0, MAXIMUM_SIGN_LETTERS - data.length());
        }

        if (!isSameItem(code + data, item)) {
            event.setOutcome(INVALID_ITEM);
            return;
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
