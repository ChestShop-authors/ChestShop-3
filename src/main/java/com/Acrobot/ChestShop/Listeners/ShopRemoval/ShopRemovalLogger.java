package com.Acrobot.ChestShop.Listeners.ShopRemoval;

import com.Acrobot.Breeze.Utils.LocationUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ShopRemovalLogger implements Listener {
    private static final String REMOVAL_MESSAGE = "%1$s was removed by %2$s - %3$s - %4$s - at %5$s";

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopRemoval(final ShopDestroyedEvent event) {
        if (Properties.LOG_ALL_SHOP_REMOVALS || event.getDestroyer() != null) {
            return;
        }

        ChestShop.getBukkitServer().getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), () -> {
            String shopOwner = ChestShopSign.getOwner(event.getSign());
            String typeOfShop = ChestShopSign.isAdminShop(shopOwner) ? "An Admin Shop" : "A shop belonging to " + shopOwner;

            String item = ChestShopSign.getQuantity(event.getSign()) + ' ' + ChestShopSign.getItem(event.getSign());
            String prices = ChestShopSign.getPrice(event.getSign());
            String location = LocationUtil.locationToString(event.getSign().getLocation());

            String message = String.format(REMOVAL_MESSAGE,
                    typeOfShop,
                    event.getDestroyer() != null ? event.getDestroyer().getName() : "???",
                    item,
                    prices,
                    location);

            ChestShop.getBukkitLogger().info(message);
        });
    }
}
