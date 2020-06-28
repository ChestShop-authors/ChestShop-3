package com.Acrobot.ChestShop.Listeners.ShopRemoval;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.UUID;

import static com.Acrobot.ChestShop.Permission.NOFEE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.AUTOFILL_CODE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class ShopRefundListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onShopDestroy(ShopDestroyedEvent event) {
        BigDecimal refundPrice = Properties.SHOP_REFUND_PRICE;

        if (event.getDestroyer() == null || Permission.has(event.getDestroyer(), NOFEE) || refundPrice.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (ChatColor.stripColor(event.getSign().getLine(ITEM_LINE)).equals(AUTOFILL_CODE)) {
            return;
        }

        AccountQueryEvent accountQueryEvent = new AccountQueryEvent(event.getSign().getLine(NAME_LINE));
        Bukkit.getPluginManager().callEvent(accountQueryEvent);
        Account account = accountQueryEvent.getAccount();
        if (account == null) {
            return;
        }

        CurrencyAddEvent currencyEvent = new CurrencyAddEvent(refundPrice, account.getUuid(), event.getSign().getWorld());
        ChestShop.callEvent(currencyEvent);

        if (NameManager.getServerEconomyAccount() != null) {
            CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(
                    refundPrice,
                    NameManager.getServerEconomyAccount().getUuid(),
                    event.getSign().getWorld());
            ChestShop.callEvent(currencySubtractEvent);
        }

        Messages.SHOP_REFUNDED.sendWithPrefix(event.getDestroyer(), "amount", Economy.formatBalance(refundPrice));
    }
}
