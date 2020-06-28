package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;

import static com.Acrobot.ChestShop.Permission.NOFEE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class CreationFeeGetter implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onShopCreation(PreShopCreationEvent event) {
        BigDecimal shopCreationPrice = Properties.SHOP_CREATION_PRICE;

        if (shopCreationPrice.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSignLine(NAME_LINE))) {
            return;
        }

        Player player = event.getPlayer();

        if (Permission.has(player, NOFEE)) {
            return;
        }

        CurrencySubtractEvent subtractionEvent = new CurrencySubtractEvent(shopCreationPrice, player);
        ChestShop.callEvent(subtractionEvent);

        if (!subtractionEvent.wasHandled()) {
            event.setOutcome(PreShopCreationEvent.CreationOutcome.NOT_ENOUGH_MONEY);
            event.setSignLines(new String[4]);
            return;
        }

        if (NameManager.getServerEconomyAccount() != null) {
            CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(
                    shopCreationPrice,
                    NameManager.getServerEconomyAccount().getUuid(),
                    player.getWorld());
            ChestShop.callEvent(currencyAddEvent);
        }

        Messages.SHOP_FEE_PAID.sendWithPrefix(player, "amount", Economy.formatBalance(shopCreationPrice));
    }
}
