package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class NameChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String name = event.getSignLine(NAME_LINE);
        Player player = event.getPlayer();
        boolean isBank = name.startsWith(uName.BANK_PREFIX);
        boolean noAccess;
        boolean exists;
        if (isBank) {
            name = uName.stripBankPrefix(name);
            exists = Economy.bankExists(name);
            noAccess = !Economy.hasBankSupport() || !Permission.has(player, Permission.BANK);
            if (Properties.BANK_MEMBERS_ALLOWED) {
                noAccess = noAccess || !Economy.isBankMember(player.getName(), name);
            } else {
                noAccess = noAccess || !Economy.isBankOwner(player.getName(), name);
            }
        } else {
            noAccess = !uName.canUseName(player, name);
            exists = Economy.hasAccount(name);
        }

        if (name.isEmpty() || !exists || (noAccess && !Permission.has(player, Permission.ADMIN))) {
            String shortName = uName.shortenName(player);
            event.setSignLine(NAME_LINE, shortName);
        }
    }
}
