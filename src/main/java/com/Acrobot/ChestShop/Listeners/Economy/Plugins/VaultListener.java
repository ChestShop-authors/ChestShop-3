package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyFormatEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyHoldEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;

/**
 * Represents a Vault connector
 *
 * @author Acrobot
 */
public class VaultListener implements Listener {
    private static Economy provider;

    private VaultListener(Economy provider) { VaultListener.provider = provider; }

    public static Economy getProvider() { return provider; }

    public boolean transactionCanFail() {
        return provider.getName().equals("Gringotts") || provider.getName().equals("GoldIsMoney") || provider.getName().equals("MultiCurrency");
    }

    /**
     * Creates a new VaultListener and returns it (if possible)
     *
     * @return VaultListener
     */
    public static @Nullable VaultListener initializeVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return null;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (rsp == null) {
            return null;
        }

        Economy provider = rsp.getProvider();

        if (provider == null) {
            return null;
        } else {
            return new VaultListener(provider);
        }
    }

    @EventHandler
    public void onAmountCheck(CurrencyAmountEvent event) {
        if (!event.getAmount().equals(BigDecimal.ZERO)) {
            return;
        }

        double balance = 0;
        //String lastSeen = NameManager.getLastSeenName(event.getAccount());
		OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        if (lastSeen != null) {
            balance = provider.getBalance(lastSeen, event.getWorld().getName());

            if (balance > Double.MAX_VALUE) {
                balance = Double.MAX_VALUE;
            }
        }

        event.setAmount(balance);
    }

    @EventHandler
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (event.hasEnough()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getAccount());
		OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        if (lastSeen != null) {
            if (provider.has(lastSeen, world.getName(), event.getDoubleAmount())) {
                event.hasEnough(true);
            }
        } else {
            event.hasEnough(false);
        }
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (event.hasAccount()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getAccount());
		OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        event.hasAccount(lastSeen != null && provider.hasAccount(lastSeen, world.getName()));
    }

    @EventHandler
    public void onCurrencyFormat(CurrencyFormatEvent event) {
        if (!event.getFormattedAmount().isEmpty()) {
            return;
        }

        String formatted = provider.format(event.getDoubleAmount());
        event.setFormattedAmount(formatted);
    }

    @EventHandler
    public void onCurrencyAdd(CurrencyAddEvent event) {
        if (event.isAdded()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getTarget());
		OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getTarget());

        if (lastSeen != null) {
            provider.depositPlayer(lastSeen, world.getName(), event.getDoubleAmount());
        }
    }

    @EventHandler
    public void onCurrencySubtraction(CurrencySubtractEvent event) {
        if (event.isSubtracted()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getTarget());
		OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getTarget());

        if (lastSeen != null) {
            provider.withdrawPlayer(lastSeen, world.getName(), event.getDoubleAmount());
        }
    }

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

        //String lastSeen = NameManager.getLastSeenName(event.getAccount());
		OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());
        String world = event.getWorld().getName();

        if (lastSeen == null) {
            event.canHold(false);
            return;
        }

        if (!provider.hasAccount(lastSeen, world)) {
            event.canHold(false);
            return;
        }

        EconomyResponse response = provider.depositPlayer(lastSeen, world, event.getDoubleAmount());

        if (!response.transactionSuccess()) {
            event.canHold(false);
            return;
        }

        provider.withdrawPlayer(lastSeen, world, event.getDoubleAmount());
    }
}
