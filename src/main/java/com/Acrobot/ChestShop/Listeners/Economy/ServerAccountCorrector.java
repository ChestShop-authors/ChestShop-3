package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.*;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.UUID;

import static com.Acrobot.ChestShop.Configuration.Properties.SERVER_ECONOMY_ACCOUNT;

/**
 * @author Acrobot
 */
public class ServerAccountCorrector implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyAdd(CurrencyAddEvent event) {
        UUID target = event.getTarget();

        if (!NameManager.isAdminShop(target) || Bukkit.getOfflinePlayer(target).getName().equals(SERVER_ECONOMY_ACCOUNT)) {
            return;
        }

        if (SERVER_ECONOMY_ACCOUNT.isEmpty()) {
            event.setAdded(true);
            return;
        } else {
            target = Bukkit.getOfflinePlayer(SERVER_ECONOMY_ACCOUNT).getUniqueId();
        }

        event.setAdded(true);

        CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(event.getAmount(), target, event.getWorld());
        ChestShop.callEvent(currencyAddEvent);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencySubtract(CurrencySubtractEvent event) {
        UUID target = event.getTarget();

        if (!NameManager.isAdminShop(target) || Bukkit.getOfflinePlayer(target).getName().equals(SERVER_ECONOMY_ACCOUNT)) {
            return;
        }

        if (SERVER_ECONOMY_ACCOUNT.isEmpty()) {
            event.setSubtracted(true);
            return;
        } else {
            target = Bukkit.getOfflinePlayer(SERVER_ECONOMY_ACCOUNT).getUniqueId();
        }

        event.setSubtracted(true);

        CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(event.getAmount(), target, event.getWorld());
        ChestShop.callEvent(currencySubtractEvent);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyCheck(CurrencyCheckEvent event) {
        UUID target = event.getAccount();

        if (!NameManager.isAdminShop(target) || Bukkit.getOfflinePlayer(target).getName().equals(SERVER_ECONOMY_ACCOUNT)) {
            return;
        }

        if (SERVER_ECONOMY_ACCOUNT.isEmpty()) {
            event.hasEnough(true);
            return;
        } else {
            target = Bukkit.getOfflinePlayer(SERVER_ECONOMY_ACCOUNT).getUniqueId();
        }

        CurrencyCheckEvent currencyCheckEvent = new CurrencyCheckEvent(event.getAmount(), target, event.getWorld());
        ChestShop.callEvent(currencyCheckEvent);

        event.hasEnough(currencyCheckEvent.hasEnough());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyHoldCheck(CurrencyHoldEvent event) {
        UUID target = event.getAccount();

        if (!NameManager.isAdminShop(target) || Bukkit.getOfflinePlayer(target).getName().equals(SERVER_ECONOMY_ACCOUNT)) {
            return;
        }

        event.canHold(true);
        event.setAccount(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onBalanceCheck(CurrencyAmountEvent event) {
        UUID target = event.getAccount();

        if (!NameManager.isAdminShop(target) || Bukkit.getOfflinePlayer(target).getName().equals(SERVER_ECONOMY_ACCOUNT)) {
            return;
        }

        if (SERVER_ECONOMY_ACCOUNT.isEmpty()) {
            event.setAmount(BigDecimal.valueOf(Double.MAX_VALUE));
            return;
        } else {
            target = Bukkit.getOfflinePlayer(SERVER_ECONOMY_ACCOUNT).getUniqueId();
        }

        CurrencyAmountEvent currencyAmountEvent = new CurrencyAmountEvent(target, event.getWorld());
        ChestShop.callEvent(currencyAmountEvent);

        event.setAmount(currencyAmountEvent.getAmount());
    }
}
