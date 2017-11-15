package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.Economy.*;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Acrobot
 */
public class ServerAccountCorrector implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyAdd(CurrencyAddEvent event) {
        UUID target = event.getTarget();

        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }

        Account account = NameManager.getServerEconomyAccount();
        target = account != null ? account.getUuid() : null;

        event.setAdded(true);
        if (target == null) {
            return;
        }

        CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(event.getAmount(), target, event.getWorld());
        ChestShop.callEvent(currencyAddEvent);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencySubtract(CurrencySubtractEvent event) {
        UUID target = event.getTarget();

        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }
    
        Account account = NameManager.getServerEconomyAccount();
        target = account != null ? account.getUuid() : null;

        event.setSubtracted(true);
        if (target == null) {
            return;
        }

        CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(event.getAmount(), target, event.getWorld());
        ChestShop.callEvent(currencySubtractEvent);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyCheck(CurrencyCheckEvent event) {
        UUID target = event.getAccount();
    
        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }

        Account account = NameManager.getServerEconomyAccount();
        target = account != null ? account.getUuid() : null;
        
        if (target == null) {
            event.hasEnough(true);
            return;
        }

        CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(event.getAmount(), target, event.getWorld());
        ChestShop.callEvent(currencyCheckEvent);

        event.hasEnough(currencyCheckEvent.hasEnough());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyHoldCheck(CurrencyHoldEvent event) {
        UUID target = event.getAccount();
    
        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }

        event.canHold(true);
        event.setAccount(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBalanceCheck(CurrencyAmountEvent event) {
        UUID target = event.getAccount();

        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }

        Account account = NameManager.getServerEconomyAccount();
        target = account != null ? account.getUuid() : null;
        
        if (target == null) {
            event.setAmount(BigDecimal.valueOf(Double.MAX_VALUE));
            return;
        }

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(target, event.getWorld());
        ChestShop.callEvent(currencyAmountEvent);

        event.setAmount(currencyAmountEvent.getAmount());
    }
}
