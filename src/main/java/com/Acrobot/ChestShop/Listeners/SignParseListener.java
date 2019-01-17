package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.MaterialParseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SignParseListener implements Listener {

    @EventHandler
    public static void onItemParse(ItemParseEvent event) {
        event.setItem(MaterialUtil.getItem(event.getItemString()));
    }

    @EventHandler
    public static void onMaterialParse(MaterialParseEvent event) {
        event.setMaterial(MaterialUtil.getMaterial(event.getMaterialString()));
    }
}
