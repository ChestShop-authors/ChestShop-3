package com.Acrobot.ChestShop.Listeners.Item;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ItemStringListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public static void calculateItemString(ItemStringQueryEvent event) {
        if (event.getItemString() == null) {
            event.setItemString(MaterialUtil.getName(event.getItem(), event.getMaxLength()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void checkValidity(ItemStringQueryEvent event) {
        ItemParseEvent parseEvent = new ItemParseEvent(event.getItemString());
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack codeItem = parseEvent.getItem();
        if (!MaterialUtil.equals(event.getItem(), codeItem)) {
            throw new IllegalArgumentException("Cannot generate code for item " + event.getItem()
                    + " with maximum length of " + event.getMaxLength()
                    + " (code " + event.getItemString() + " results in item " + codeItem + ")");
        }
    }

}
