package com.Acrobot.ChestShop.Economy;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Economy.CurrencyAddEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyCheckEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyFormatEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencySubtractEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Acrobot
 *         Economy management
 */
public class Economy {
    /**
     * Get the name of the server conomy account
     * @return The username of te server economy account
     * @deprecated Use {@link NameManager#getServerEconomyAccount()} or {@link Properties#SERVER_ECONOMY_ACCOUNT}
     */
    @Deprecated
    public static String getServerAccountName() {
        return Properties.SERVER_ECONOMY_ACCOUNT;
    }

    public static boolean isOwnerEconomicallyActive(Inventory inventory) {
        return !ChestShopSign.isAdminShop(inventory) || NameManager.getServerEconomyAccount() != null;
    }

    /**
     * @deprecated Directly call the {@link CurrencyAddEvent}
     */
    @Deprecated
    public static boolean add(UUID name, World world, double amount) {
        CurrencyAddEvent event = new CurrencyAddEvent(BigDecimal.valueOf(amount), name, world);
        ChestShop.callEvent(event);

        return event.wasHandled();
    }

    /**
     * @deprecated Directly call the {@link CurrencySubtractEvent}
     */
    @Deprecated
    public static boolean subtract(UUID name, World world, double amount) {
        CurrencySubtractEvent event = new CurrencySubtractEvent(BigDecimal.valueOf(amount), name, world);
        ChestShop.callEvent(event);

        return event.wasHandled();
    }

    /**
     * @deprecated Directly call the {@link CurrencyCheckEvent}
     */
    @Deprecated
    public static boolean hasEnough(UUID name, World world, double amount) {
        CurrencyCheckEvent event = new CurrencyCheckEvent(BigDecimal.valueOf(amount), name, world);
        ChestShop.callEvent(event);

        return event.hasEnough();
    }

    public static String formatBalance(BigDecimal amount) {
        CurrencyFormatEvent event = new CurrencyFormatEvent(amount);
        ChestShop.callEvent(event);

        return event.getFormattedAmount();
    }

    /**
     * @deprecated Use {@link #formatBalance(BigDecimal)}
     */
    @Deprecated
    public static String formatBalance(double amount) {
        return formatBalance(BigDecimal.valueOf(amount));
    }
}
