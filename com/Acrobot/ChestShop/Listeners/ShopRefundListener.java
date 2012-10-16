package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Config.Property.SHOP_REFUND_PRICE;
import static com.Acrobot.ChestShop.Permission.NOFEE;

/**
 * @author Acrobot
 */
public class ShopRefundListener implements Listener {
    @EventHandler
    public static void onShopDestroy(ShopDestroyedEvent event) {
        float refundPrice = Config.getFloat(SHOP_REFUND_PRICE);

        if (event.getDestroyer() == null || Permission.has(event.getDestroyer(), NOFEE) || refundPrice == 0) {
            return;
        }

        String ownerName = uName.getName(event.getSign().getLine(ChestShopSign.NAME_LINE));
        Economy.add(ownerName, refundPrice);

        event.getDestroyer().sendMessage(Config.getLocal(Language.SHOP_REFUNDED).replace("%amount", Economy.formatBalance(refundPrice)));
    }
}
