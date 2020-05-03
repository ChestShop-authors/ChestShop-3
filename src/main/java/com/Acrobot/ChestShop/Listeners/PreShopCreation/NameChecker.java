package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Permission.OTHER_NAME_CREATE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.UNKNOWN_PLAYER;

/**
 * @author Acrobot
 */
public class NameChecker implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        handleEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPreShopCreationHighest(PreShopCreationEvent event) {
        handleEvent(event);
    }

    private static void handleEvent(PreShopCreationEvent event) {
        String name = event.getSignLine(NAME_LINE);
        Player player = event.getPlayer();

        Account account = event.getOwnerAccount();
        if (account == null || !account.getShortName().equalsIgnoreCase(name)) {
            account = null;
            try {
                if (name.isEmpty() || !NameManager.canUseName(player, OTHER_NAME_CREATE, name)) {
                    account = NameManager.getOrCreateAccount(player);
                } else {
                    AccountQueryEvent accountQueryEvent = new AccountQueryEvent(name);
                    Bukkit.getPluginManager().callEvent(accountQueryEvent);
                    account = accountQueryEvent.getAccount();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        event.setOwnerAccount(account);
        if (account != null) {
            event.setSignLine(NAME_LINE, account.getShortName());
        } else {
            event.setSignLine(NAME_LINE, "");
            event.setOutcome(UNKNOWN_PLAYER);
        }
    }
}
