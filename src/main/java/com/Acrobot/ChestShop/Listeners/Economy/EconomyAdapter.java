package com.Acrobot.ChestShop.Listeners.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAmountEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyFormatEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyHoldEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyTransferEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class EconomyAdapter implements Listener {

    protected static final ArrayList<Consumer<ProviderInfo>> providerChangeListeners = new ArrayList<>();

    protected static void notifyProviderChangeListeners(ProviderInfo providerInfo) {
        providerChangeListeners.forEach(listener -> listener.accept(providerInfo));
    }

    public void registerProviderChangeListener(Consumer<ProviderInfo> listener) {
        providerChangeListeners.add(listener);
    }

    public abstract void onAmountCheck(CurrencyAmountEvent event);

    public abstract void onCurrencyCheck(CurrencyCheckEvent event);

    public abstract void onAccountCheck(AccountCheckEvent event);

    public abstract void onCurrencyFormat(CurrencyFormatEvent event);

    public abstract void onCurrencyAdd(CurrencyAddEvent event);

    public abstract void onCurrencySubtraction(CurrencySubtractEvent event);

    public abstract void onCurrencyTransfer(CurrencyTransferEvent event);

    public abstract void onCurrencyHoldCheck(CurrencyHoldEvent event);

    /**
     * Convenience method to process transfers by first subtracting and then adding
     *
     * @param event The CurrencyTransferEvent to process
     */
    protected void processTransfer(CurrencyTransferEvent event) {
        if (event.wasHandled()) {
            return;
        }

        BigDecimal amountSent = event.getAmountSent();
        CurrencySubtractEvent currencySubtractEvent = new CurrencySubtractEvent(amountSent, event.getSender(), event.getWorld());
        if (!NameManager.isAdminShop(event.getSender())) {
            ChestShop.callEvent(currencySubtractEvent);
        } else {
            currencySubtractEvent.setHandled(true);
        }

        if (!currencySubtractEvent.wasHandled()) {
            return;
        }

        BigDecimal amountReceived = event.getAmountReceived().subtract(amountSent.subtract(currencySubtractEvent.getAmount()));
        CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(amountReceived, event.getReceiver(), event.getWorld());
        if (!NameManager.isAdminShop(event.getReceiver())) {
            ChestShop.callEvent(currencyAddEvent);
        } else {
            currencyAddEvent.setHandled(true);
        }

        if (currencyAddEvent.wasHandled()) {
            event.setHandled(true);
        } else {
            CurrencyAddEvent currencyResetEvent = new CurrencyAddEvent(
                    currencySubtractEvent.getAmount(),
                    event.getSender(),
                    event.getWorld()
            );
            ChestShop.callEvent(currencyResetEvent);
        }
    }

    public static class ProviderInfo {
        private final String name;
        private final String version;

        public ProviderInfo(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }
    }
}
