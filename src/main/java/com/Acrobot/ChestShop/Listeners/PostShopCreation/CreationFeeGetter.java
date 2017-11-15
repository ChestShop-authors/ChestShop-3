package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.math.BigDecimal;

import static com.Acrobot.ChestShop.Permission.NOFEE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class CreationFeeGetter implements Listener {

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        double shopCreationPrice = Properties.SHOP_CREATION_PRICE;

        if (shopCreationPrice == 0) {
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSignLine(NAME_LINE))) {
            return;
        }

        Player player = event.getPlayer();

        if (Permission.has(player, NOFEE)) {
            return;
        }

        CurrencySubtractEvent subtractionEvent = new CurrencySubtractEvent(BigDecimal.valueOf(shopCreationPrice), player);
        ChestShop.callEvent(subtractionEvent);

        if (NameManager.getServerEconomyAccount() != null) {
            CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(
                    BigDecimal.valueOf(shopCreationPrice),
                    NameManager.getServerEconomyAccount().getUuid(),
                    player.getWorld());
            ChestShop.callEvent(currencyAddEvent);
        }

        player.sendMessage(Messages.prefix(Messages.SHOP_FEE_PAID.replace("%amount", Economy.formatBalance(shopCreationPrice))));
    }
}
