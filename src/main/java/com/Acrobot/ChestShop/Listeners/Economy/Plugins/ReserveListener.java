package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyFormatEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyHoldEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Represents a Reserve connector
 *
 * @author creatorfromhell
 */
public class ReserveListener implements Listener {

    private static @Nullable EconomyAPI economyAPI;

    public ReserveListener(EconomyAPI api) {
        ReserveListener.economyAPI = api;
    }

    public static EconomyAPI getProvider() {
      return economyAPI;
    }

    public boolean provided() {
        if(Bukkit.getPluginManager().getPlugin("Reserve") == null) {
            return false;
        }
        return economyAPI != null;
    }

    public boolean transactionCanFail() {
        if(!provided()) return false;
        return economyAPI.name().equals("Gringotts") || economyAPI.name().equals("GoldIsMoney") ||
                economyAPI.name().equals("MultiCurrency") ||
                economyAPI.name().equalsIgnoreCase("TheNewEconomy");
    }

    public static @Nullable ReserveListener prepareListener() {
        EconomyAPI api = null;
        if(Bukkit.getPluginManager().getPlugin("Reserve") != null) {
            if(Reserve.instance().economyProvided()) api = Reserve.instance().economy();
        }

        if(api == null) return null;
        return new ReserveListener(api);
    }

    @EventHandler
    public void onAmountCheck(CurrencyAmountEvent event) {
        if (!event.getAmount().equals(BigDecimal.ZERO)) {
            return;
        }
        final OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        if (lastSeen != null && provided()) {
            event.setAmount(economyAPI.getHoldings(event.getAccount(), event.getWorld().getName()));
        }
    }

    @EventHandler
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (event.hasEnough()) {
            return;
        }
        final OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        if (lastSeen != null && provided()) {
            event.hasEnough(economyAPI.hasHoldings(event.getAccount(),
                                                                                         event.getAmount(),
                                                                                         event.getWorld().getName()));
        }
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (event.hasAccount()) {
            return;
        }
        final OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());
        event.hasAccount(lastSeen != null && provided() && economyAPI.hasAccount(event.getAccount()));
    }

    @EventHandler
    public void onCurrencyFormat(CurrencyFormatEvent event) {
        if (!event.getFormattedAmount().isEmpty()) {
            return;
        }

        if (provided()) {
            event.setFormattedAmount(economyAPI.format(event.getAmount()));
        }
    }

    @EventHandler
    public void onCurrencyAdd(CurrencyAddEvent event) {
        if (event.isAdded()) {
            return;
        }
        final OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getTarget());

        if (lastSeen != null && provided()) {
            economyAPI.addHoldings(event.getTarget(), event.getAmount(), event.getWorld().getName());
        }
    }

    @EventHandler
    public void onCurrencySubtraction(CurrencySubtractEvent event) {
        if (event.isSubtracted()) {
            return;
        }
        final OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getTarget());

        if (lastSeen != null && provided()) {
            economyAPI.removeHoldings(event.getTarget(), event.getAmount(), event.getWorld().getName());
        }
    }

    //Copied from VaultListener as Reserve doesn't have a dedicated transfer api until 1.0.11
    @EventHandler
    public static void onCurrencyTransfer(CurrencyTransferEvent event) {
        if (event.hasBeenTransferred()) {
            return;
        }

        CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(event.getAmount(), event.getSender(), event.getWorld());
        ChestShop.callEvent(currencySubtractEvent);

        if (!currencySubtractEvent.isSubtracted()) {
            return;
        }

        CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(currencySubtractEvent.getAmount(), event.getReceiver(), event.getWorld());
        ChestShop.callEvent(currencyAddEvent);
    }

    @EventHandler
    public void onCurrencyHoldCheck(CurrencyHoldEvent event) {
        if (event.getAccount() == null || !transactionCanFail()) {
            return;
        }

        final OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        if (lastSeen == null || !provided()) {
            event.canHold(false);
            return;
        }

        final String world = event.getWorld().getName();
        if (!economyAPI.hasAccount(event.getAccount())) {
            event.canHold(false);
            return;
        }

        if (!economyAPI.addHoldings(event.getAccount(), event.getAmount(), world)) {
            event.canHold(false);
            return;
        }
        economyAPI.removeHoldings(event.getAccount(), event.getAmount(), world);
    }
}
