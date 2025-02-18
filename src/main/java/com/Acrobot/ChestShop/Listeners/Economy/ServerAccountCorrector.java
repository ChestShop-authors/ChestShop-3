package com.Acrobot.ChestShop.Listeners.Economy;

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

        if (target == null) {
            event.setHandled(true);
            return;
        }

        event.setTarget(target);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencySubtract(CurrencySubtractEvent event) {
        UUID target = event.getTarget();

        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }

        Account account = NameManager.getServerEconomyAccount();
        target = account != null ? account.getUuid() : null;

        if (target == null) {
            event.setHandled(true);
            return;
        }

        event.setTarget(target);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyTransfer(CurrencyTransferEvent event) {
        UUID partner = event.getPartner();

        if (!NameManager.isAdminShop(partner) || NameManager.isServerEconomyAccount(partner)) {
            return;
        }

        Account account = NameManager.getServerEconomyAccount();
        partner = account != null ? account.getUuid() : null;

        if (partner == null) {
            return;
        }

        event.setPartner(partner);
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

        event.setAccount(target);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onCurrencyHoldCheck(CurrencyHoldEvent event) {
        UUID target = event.getAccount();

        if (!NameManager.isAdminShop(target) || NameManager.isServerEconomyAccount(target)) {
            return;
        }

        Account account = NameManager.getServerEconomyAccount();
        target = account != null ? account.getUuid() : null;

        if (target == null) {
            event.canHold(true);
            return;
        }

        event.setAccount(target);
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

        event.setAccount(target);
    }
}
