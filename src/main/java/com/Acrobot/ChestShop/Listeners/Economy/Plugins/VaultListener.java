package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Represents a Vault connector
 *
 * @author Acrobot
 */
public class VaultListener implements Listener {
    private final Economy provider;

    private VaultListener(Economy provider) {
        this.provider = provider;
    }

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

        double balance = provider.getBalance(Bukkit.getOfflinePlayer(event.getAccount()), event.getWorld().getName());

        if (balance > Double.MAX_VALUE) {
            balance = Double.MAX_VALUE;
        }

        event.setAmount(balance);
    }

    @EventHandler
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (event.hasEnough()) {
            return;
        }

        World world = event.getWorld();

        if (provider.has(Bukkit.getOfflinePlayer(event.getAccount()), world.getName(), event.getDoubleAmount())) {
            event.hasEnough(true);
        }
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (event.hasAccount()) {
            return;
        }

        World world = event.getWorld();

        if (!provider.hasAccount(Bukkit.getOfflinePlayer(event.getAccount()), world.getName())) {
            event.hasAccount(false);
        }
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

        provider.depositPlayer(Bukkit.getOfflinePlayer(event.getTarget()), world.getName(), event.getDoubleAmount());
    }

    @EventHandler
    public void onCurrencySubtraction(CurrencySubtractEvent event) {
        if (event.isSubtracted()) {
            return;
        }

        World world = event.getWorld();

        provider.withdrawPlayer(Bukkit.getOfflinePlayer(event.getTarget()), world.getName(), event.getDoubleAmount());
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

        if (!provider.hasAccount(Bukkit.getOfflinePlayer(event.getAccount()), event.getWorld().getName())) {
            event.canHold(false);
            return;
        }

        EconomyResponse response = provider.depositPlayer(Bukkit.getOfflinePlayer(event.getAccount()), event.getWorld().getName(), event.getDoubleAmount());

        if (!response.transactionSuccess()) {
            event.canHold(false);
            return;
        }

        provider.withdrawPlayer(Bukkit.getOfflinePlayer(event.getAccount()), event.getWorld().getName(), event.getDoubleAmount());
    }
}
