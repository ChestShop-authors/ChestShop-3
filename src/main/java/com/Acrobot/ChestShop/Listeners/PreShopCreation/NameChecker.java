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
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.UNKNOWN_PLAYER;

/**
 * @author Acrobot
 */
public class NameChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String name = event.getSignLine(NAME_LINE);
        Player player = event.getPlayer();
        event.setSignLine(NAME_LINE, "");
        event.setOutcome(UNKNOWN_PLAYER);

        if (name.isEmpty() || !NameManager.canUseName(player, OTHER_NAME_CREATE, name)) {
            Account account = NameManager.getOrCreateAccount(player);
            if (account != null) {
                event.setSignLine(NAME_LINE, account.getShortName());
                event.setOutcome(SHOP_CREATED_SUCCESSFULLY);
            }
        } else {
            AccountQueryEvent accountQueryEvent = new AccountQueryEvent(name);
            Bukkit.getPluginManager().callEvent(accountQueryEvent);
            Account account = accountQueryEvent.getAccount();
            if (account != null) {
                event.setSignLine(NAME_LINE, account.getShortName());
                event.setOutcome(SHOP_CREATED_SUCCESSFULLY);
            }
        }
    }
}
