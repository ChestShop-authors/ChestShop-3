package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ShopCreationLogger implements Listener {
    private static final String CREATION_MESSAGE = "%1$s created %2$s - %3$s - %4$s - at %5$s";

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopCreation(final ShopCreatedEvent event) {
        ChestShop.getBukkitServer().getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), () -> {
            String creator = event.getPlayer().getName();
            String shopOwner = ChestShopSign.getOwner(event.getSignLines());
            String typeOfShop = ChestShopSign.isAdminShop(shopOwner) ? "an Admin Shop" : "a shop" + (event.createdByOwner() ? "" : " for " + event.getOwnerAccount().getName());

            String item = ChestShopSign.getQuantity(event.getSignLines()) + ' ' + ChestShopSign.getItem(event.getSignLines());
            String prices = ChestShopSign.getPrice(event.getSignLines());
            String location = LocationUtil.locationToString(event.getSign().getLocation());

            String message = String.format(CREATION_MESSAGE,
                    creator,
                    typeOfShop,
                    item,
                    prices,
                    location);

            ChestShop.getBukkitLogger().info(message);
        });
    }
}
