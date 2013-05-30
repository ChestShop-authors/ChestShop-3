package com.Acrobot.ChestShop.Listeners.Economy;

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
        paymentMethod.getAccount(event.getAccount()).hasEnough(event.getDoubleAmount());
    }

    @EventHandler
    public void onAccountCheck(AccountCheckEvent event) {
        paymentMethod.hasAccount(event.getAccount());
    }

    @EventHandler
    public void onCurrencyFormat(CurrencyFormatEvent event) {
        String formatted = paymentMethod.format(event.getDoubleAmount());

        event.setFormattedAmount(formatted);
    }

    @EventHandler
    public void onCurrencyAdd(CurrencyAddEvent event) {
        paymentMethod.getAccount(event.getTarget()).add(event.getDoubleAmount());
    }

    @EventHandler
    public void onCurrencySubtract(CurrencySubtractEvent event) {
        paymentMethod.getAccount(event.getTarget()).subtract(event.getDoubleAmount());
    }

    @EventHandler
    public void onCurrencyTransfer(CurrencyTransferEvent event) {
        boolean subtracted = paymentMethod.getAccount(event.getSender()).subtract(event.getDoubleAmount());

        if (subtracted) {
            paymentMethod.getAccount(event.getReceiver()).add(event.getDoubleAmount());
        }
    }
}
