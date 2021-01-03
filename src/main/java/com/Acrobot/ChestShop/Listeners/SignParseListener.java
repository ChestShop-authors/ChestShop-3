package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.MaterialParseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SignParseListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public static void onItemParse(ItemParseEvent event) {
        if (event.getItem() == null) {
            event.setItem(MaterialUtil.getItem(event.getItemString()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public static void onMaterialParse(MaterialParseEvent event) {
        if (event.getMaterial() == null) {
            event.setMaterial(MaterialUtil.getMaterial(event.getMaterialString()));
        }
    }
}
