package com.Acrobot.ChestShop.Listeners.Economy.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.Economy.*;
import com.nijikokun.register.payment.forChestShop.Method;
import com.nijikokun.register.payment.forChestShop.Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;

/**
 * Represents a Register connector
 *
 * @author Acrobot
 */
public class RegisterListener implements Listener {
    private Method paymentMethod;

    private RegisterListener(Method paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static @Nullable RegisterListener initializeRegister() {
        Method method = Methods.load();

        if (method == null) {
            return null;
        }

        return new RegisterListener(method);
    }

    @EventHandler
    public void onCurrencyCheck(CurrencyCheckEvent event) {
        if (event.hasEnough()) {
            return;
        }

        boolean check = paymentMethod.getAccount(event.getAccount()).hasEnough(event.getDoubleAmount());
        event.hasEnough(check);
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        if (event.hasAccount()) {
            return;
        }

        boolean check = paymentMethod.hasAccount(event.getAccount());
        event.hasAccount(check);
    }

    @EventHandler
    public void onCurrencyFormat(CurrencyFormatEvent event) {
        if (!event.getFormattedAmount().isEmpty()) {
            return;
        }

        String formatted = paymentMethod.format(event.getDoubleAmount());
        event.setFormattedAmount(formatted);
    }

    @EventHandler
    public void onCurrencyAdd(CurrencyAddEvent event) {
        if (event.isAdded()) {
            return;
        }

        paymentMethod.getAccount(event.getTarget()).add(event.getDoubleAmount());
        event.setAdded(true);
    }

    @EventHandler
    public void onCurrencySubtract(CurrencySubtractEvent event) {
        if (event.isSubtracted()) {
            return;
        }

        paymentMethod.getAccount(event.getTarget()).subtract(event.getDoubleAmount());
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

        CurrencyAddEvent currencyAddEvent = new CurrencyAddEvent(event.getAmount(), event.getReceiver(), event.getWorld());
        ChestShop.callEvent(currencyAddEvent);
    }
}
