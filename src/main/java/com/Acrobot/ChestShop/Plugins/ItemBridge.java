package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import com.jojodmo.itembridge.ItemBridgeKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.StringUtil.getMinecraftStringWidth;

/**
 * @author Acrobot
 */
public class ItemBridge implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onItemParse(ItemParseEvent event) {
        if (event.getItem() == null) {
            ItemStack item = com.jojodmo.itembridge.ItemBridge.getItemStack(event.getItemString());
            if (item != null) {
                event.setItem(item);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemStringQuery(ItemStringQueryEvent event) {
        ItemBridgeKey key = com.jojodmo.itembridge.ItemBridge.getItemKey(event.getItem());
        // If namespace is "minecraft" then we ignore it and use our own logic
        if (key != null && !"minecraft".equalsIgnoreCase(key.getNamespace())) {
            String code = key.toString();
            // Make sure the ItemBridge string is not too long as we can't parse shortened ones
            if (event.getMaxWidth() > 0) {
                int width = getMinecraftStringWidth(code);
                if (width > event.getMaxWidth()) {
                    ChestShop.logDebug("Can't use ItemBridge alias " + code + " as it's width (" + width + ") was wider than the allowed max width of " + event.getMaxWidth());
                    return;
                }
            }
            event.setItemString(key.toString());
        }
    }
}
