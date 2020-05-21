package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyFormatEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyHoldEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
import com.Acrobot.ChestShop.Listeners.Economy.EconomyAdapter;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Represents a Reserve connector
 *
 * @author creatorfromhell
 */
public class ReserveListener extends EconomyAdapter {

    private static @Nullable EconomyAPI economyAPI;

    public ReserveListener(EconomyAPI api) {
        ReserveListener.economyAPI = api;
    }

    public static EconomyAPI getProvider() {
        return economyAPI;
    }

    public boolean provided() {
        return economyAPI != null;
    }

    public boolean transactionCanFail() {
        if (economyAPI == null) {
            return false;
        }

        return economyAPI.name().equals("Gringotts")
                || economyAPI.name().equals("GoldIsMoney")
                || economyAPI.name().equals("MultiCurrency")
                || economyAPI.name().equalsIgnoreCase("TheNewEconomy");
    }

    public static @Nullable ReserveListener prepareListener() {
        if (Bukkit.getPluginManager().getPlugin("Reserve") == null || !Reserve.instance().economyProvided()) {
            return null;
        }

        EconomyAPI api = Reserve.instance().economy();

        if (api == null) {
            return null;
        } else {
            return new ReserveListener(api);
        }
    }

    @EventHandler
    public void onAmountCheck(CurrencyAmountEvent event) {
        if (!provided() || event.wasHandled() || !event.getAmount().equals(BigDecimal.ZERO)) {
            return;
        }
        event.setAmount(economyAPI.getHoldings(event.getAccount(), event.getWorld().getName()));
        event.setHandled(true);
    }

    @EventHandler
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (!provided() || event.wasHandled() || event.hasEnough()) {
            return;
        }
        event.hasEnough(economyAPI.hasHoldings(event.getAccount(),
                event.getAmount(),
                event.getWorld().getName()));
        event.setHandled(true);
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (!provided() || event.wasHandled() || event.hasAccount()) {
            return;
        }
        event.hasAccount(economyAPI.hasAccount(event.getAccount()));
        event.setHandled(true);
    }

    @EventHandler
    public void onCurrencyFormat(CurrencyFormatEvent event) {
        if ( event.wasHandled() || !event.getFormattedAmount().isEmpty()) {
            return;
        }

        if (provided()) {
            event.setFormattedAmount(economyAPI.format(event.getAmount()));
            event.setHandled(true);
        }
    }

    @EventHandler
    public void onCurrencyAdd(CurrencyAddEvent event) {
        if (!provided() || event.wasHandled()) {
            return;
        }
        event.setHandled(economyAPI.addHoldings(event.getTarget(), event.getAmount(), event.getWorld().getName()));
    }

    @EventHandler
    public void onCurrencySubtraction(CurrencySubtractEvent event) {
        if (!provided() || event.wasHandled()) {
            return;
        }
        event.setHandled(economyAPI.removeHoldings(event.getTarget(), event.getAmount(), event.getWorld().getName()));
    }

    @EventHandler
    public void onCurrencyTransfer(CurrencyTransferEvent event) {
        processTransfer(event);
    }

    @EventHandler
    public void onCurrencyHoldCheck(CurrencyHoldEvent event) {
        if (event.getAccount() == null || event.wasHandled() || !transactionCanFail() || event.canHold()) {
            return;
        }

        final String world = event.getWorld().getName();
        if (!economyAPI.hasAccount(event.getAccount())) {
            event.canHold(false);
            return;
        }

        event.canHold(economyAPI.canAddHoldings(event.getAccount(), event.getAmount(), world));
        event.setHandled(true);
    }
}
