package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import com.Acrobot.ChestShop.Events.Economy.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.annotation.Nullable;

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

    /**
     * Creates a new VaultListener and returns it (if possible)
     *
     * @return VaultListener
     */
    public static @Nullable VaultListener initializeVault() {
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
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (event.hasEnough()) {
            return;
        }

        World world = event.getWorld();

        if (!provider.has(event.getAccount(), world.getName(), event.getDoubleAmount())) {
            event.hasEnough(false);
        }
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (event.hasAccount()) {
            return;
        }

        World world = event.getWorld();

        if (!provider.hasAccount(event.getAccount(), world.getName())) {
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

        provider.depositPlayer(event.getTarget(), world.getName(), event.getDoubleAmount());
    }

    @EventHandler
    public void onCurrencySubtraction(CurrencySubtractEvent event) {
        if (event.isSubtracted()) {
            return;
        }

        World world = event.getWorld();

        provider.withdrawPlayer(event.getTarget(), world.getName(), event.getDoubleAmount());
    }

    @EventHandler
    public void onCurrencyTransfer(CurrencyTransferEvent event) {
        if (event.hasBeenTransferred()) {
            return;
        }

        World world = event.getWorld();

        EconomyResponse response = provider.withdrawPlayer(event.getSender(), world.getName(), event.getDoubleAmount());

        if (response.transactionSuccess()) {
            provider.depositPlayer(event.getReceiver(), world.getName(), event.getDoubleAmount());
        }
    }
}
