package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import java.math.BigDecimal;
import java.util.logging.Level;

import javax.annotation.Nullable;

import com.Acrobot.ChestShop.Listeners.Economy.EconomyAdapter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
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
public class VaultListener extends EconomyAdapter {
    private RegisteredServiceProvider<Economy> rsp;
    private static Economy provider;

    private VaultListener() {
        updateEconomyProvider();
    }

    private void updateEconomyProvider() {
        rsp = ChestShop.getBukkitServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            provider = rsp.getProvider();
            ChestShop.getBukkitLogger().log(Level.INFO, "Using " + provider.getName() + " as the Economy provider now.");
        }
    }

    private boolean checkSetup() {
        if (provider == null) {
            ChestShop.getBukkitLogger().log(Level.SEVERE, "No Vault compatible Economy plugin found!");
            ChestShop.getBukkitServer().getPluginManager().disablePlugin(ChestShop.getPlugin());
            return false;
        }
        return true;
    }

    public static Economy getProvider() { return provider; }

    public boolean transactionCanFail() {
        if (provider == null) {
            return false;
        }

        return provider.getName().equals("Gringotts")
                || provider.getName().equals("GoldIsMoney")
                || provider.getName().equals("MultiCurrency")
                || provider.getName().equalsIgnoreCase("TheNewEconomy");
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
        return new VaultListener();
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getProvider() instanceof Economy) {
            updateEconomyProvider();
        }
    }

    @EventHandler
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        if (event.getProvider().getProvider() instanceof Economy) {
            updateEconomyProvider();
        }
    }

    @EventHandler
    public void onAmountCheck(CurrencyAmountEvent event) {
        if (!checkSetup() || event.wasHandled() || !event.getAmount().equals(BigDecimal.ZERO)) {
            return;
        }

        double balance = 0;
        OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        if (lastSeen != null) {
            try {
                balance = provider.getBalance(lastSeen, event.getWorld().getName());
            } catch (Exception e) {
                ChestShop.getBukkitLogger().log(Level.WARNING, "Could not get balance account of " + lastSeen.getUniqueId() + "/" + lastSeen.getName() + "." +
                        "This is probably due to https://github.com/MilkBowl/Vault/issues/746 and has to be fixed in either Vault directly or your economy plugin." +
                        "If you are sure it's not this issue then please report the following error.", e);
            }

            if (balance > Double.MAX_VALUE) {
                balance = Double.MAX_VALUE;
            }
            event.setHandled(true);
        } else {
            ChestShop.getBukkitLogger().log(Level.WARNING, "The server could not get the OfflinePlayer with the UUID " + event.getAccount() + " to check balance?");
        }

        event.setAmount(BigDecimal.valueOf(balance));
    }

    @EventHandler
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (!checkSetup() || event.wasHandled() || event.hasEnough()) {
            return;
        }

        World world = event.getWorld();
        OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        try {
            event.hasEnough(lastSeen != null && provider.has(lastSeen, world.getName(), event.getAmount().doubleValue()));
            event.setHandled(true);
        } catch (Exception e) {
            ChestShop.getBukkitLogger().log(Level.WARNING, "Could not check if account of " + lastSeen.getUniqueId() + "/" + lastSeen.getName() + " has " + event.getAmount() + "." +
                    "This is probably due to https://github.com/MilkBowl/Vault/issues/746 and has to be fixed in either Vault directly or your economy plugin." +
                    "If you are sure it's not this issue then please report the following error.", e);
        }
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (!checkSetup() || event.wasHandled() || event.hasAccount()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getAccount());
        OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());

        try {
            event.hasAccount(lastSeen != null && provider.hasAccount(lastSeen, world.getName()));
            event.setHandled(true);
        } catch (Exception e) {
            ChestShop.getBukkitLogger().log(Level.WARNING, "Could not check account balance of "+ lastSeen.getUniqueId() + "/" + lastSeen.getName() + "." +
                    "This is probably due to https://github.com/MilkBowl/Vault/issues/746 and has to be fixed in either Vault directly or your economy plugin." +
                    "If you are sure it's not this issue then please report the following error.", e);
        }
    }

    @EventHandler
    public void onCurrencyFormat(CurrencyFormatEvent event) {
        if (!checkSetup() || event.wasHandled() || !event.getFormattedAmount().isEmpty()) {
            return;
        }

        String formatted = provider.format(event.getAmount().doubleValue());
        event.setFormattedAmount(formatted);
        event.setHandled(true);
    }

    @EventHandler
    public void onCurrencyAdd(CurrencyAddEvent event) {
        if (!checkSetup() || event.wasHandled()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getTarget());
        OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getTarget());

        if (lastSeen != null) {
            try {
                EconomyResponse response = provider.depositPlayer(lastSeen, world.getName(), event.getAmount().doubleValue());
                event.setHandled(response.type == EconomyResponse.ResponseType.SUCCESS);
            } catch (Exception e) {
                ChestShop.getBukkitLogger().log(Level.WARNING, "Could not add money to account of " + lastSeen.getUniqueId() + "/" + lastSeen.getName() + "." +
                        "This is probably due to https://github.com/MilkBowl/Vault/issues/746 and has to be fixed in either Vault directly or your economy plugin." +
                        "If you are sure it's not this issue then please report the following error.", e);
            }
        } else {
            ChestShop.getBukkitLogger().log(Level.WARNING, "The server could not get the OfflinePlayer with the UUID " + event.getTarget() + " to add " + event.getAmount() + "?");
        }
    }

    @EventHandler
    public void onCurrencySubtraction(CurrencySubtractEvent event) {
        if (!checkSetup() || event.wasHandled()) {
            return;
        }

        World world = event.getWorld();
        //String lastSeen = NameManager.getLastSeenName(event.getTarget());
        OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getTarget());

        if (lastSeen != null) {
            try {
                EconomyResponse response = provider.withdrawPlayer(lastSeen, world.getName(), event.getAmount().doubleValue());
                event.setHandled(response.type == EconomyResponse.ResponseType.SUCCESS);
            } catch (Exception e) {
                ChestShop.getBukkitLogger().log(Level.WARNING, "Could not add money to account of " + lastSeen.getUniqueId() + "/" + lastSeen.getName() + "." +
                        "This is probably due to https://github.com/MilkBowl/Vault/issues/746 and has to be fixed in either Vault directly or your economy plugin." +
                        "If you are sure it's not this issue then please report the following error.", e);
            }
        } else {
            ChestShop.getBukkitLogger().log(Level.WARNING, "The server could not get the OfflinePlayer with the UUID " + event.getTarget() + " to subtract " + event.getAmount() + "?");
        }
    }

    @EventHandler
    public void onCurrencyTransfer(CurrencyTransferEvent event) {
        if (!checkSetup()) {
            return;
        }
        processTransfer(event);
    }

    @EventHandler
    public void onCurrencyHoldCheck(CurrencyHoldEvent event) {
        if (!checkSetup() || event.wasHandled() || event.getAccount() == null || !transactionCanFail() || event.canHold()) {
            return;
        }

        //String lastSeen = NameManager.getLastSeenName(event.getAccount());
        OfflinePlayer lastSeen = Bukkit.getOfflinePlayer(event.getAccount());
        String world = event.getWorld().getName();

        if (lastSeen == null) {
            event.canHold(false);
            ChestShop.getBukkitLogger().log(Level.WARNING, "The server could not get the OfflinePlayer with the UUID " + event.getAccount() + " to check if it can hold " + event.getAmount() + "?");
            return;
        }

        try {
            if (!provider.hasAccount(lastSeen, world)) {
                event.canHold(false);
                return;
            }

            EconomyResponse response = provider.depositPlayer(lastSeen, world, event.getAmount().doubleValue());

            if (!response.transactionSuccess()) {
                event.canHold(false);
                event.setHandled(true);
                return;
            }

            provider.withdrawPlayer(lastSeen, world, event.getAmount().doubleValue());
            event.setHandled(true);
        } catch (Exception e) {
            ChestShop.getBukkitLogger().log(Level.WARNING, "Could not check if account of " + lastSeen.getUniqueId() + "/" + lastSeen.getName() + " can hold " + event.getAmount() + "." +
                    "This is probably due to https://github.com/MilkBowl/Vault/issues/746 and has to be fixed in either Vault directly or your economy plugin." +
                    "If you are sure it's not then please report this error to the devs of ChestShop.", e);
        }
    }
}
