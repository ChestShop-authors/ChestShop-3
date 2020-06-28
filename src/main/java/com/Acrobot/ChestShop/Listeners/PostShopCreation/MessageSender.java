package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class MessageSender implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopCreation(ShopCreatedEvent event) {
        Messages.SHOP_CREATED.sendWithPrefix(event.getPlayer());
    }
}
