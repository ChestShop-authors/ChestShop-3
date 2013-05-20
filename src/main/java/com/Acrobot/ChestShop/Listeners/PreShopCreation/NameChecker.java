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

        if (name.startsWith(uName.BANK_PREFIX)) {
            name = uName.stripBankPrefix(name);
            boolean bankExists = Economy.bankExists(name);
            boolean hasAccess = Economy.hasBankSupport() && Permission.has(player, Permission.BANK);

            if (Properties.BANK_MEMBERS_ALLOWED) {
                hasAccess = hasAccess && Economy.isBankMember(player.getName(), name);
            } else {
                hasAccess = hasAccess && Economy.isBankOwner(player.getName(), name);
            }

            if (!bankExists || (!hasAccess && !Permission.has(player, Permission.ADMIN))) {
                event.setSignLine(NAME_LINE, uName.stripName(player));
            }

            return;
        }

        if (name.isEmpty() || (!uName.canUseName(player, name) && !Permission.has(player, Permission.ADMIN))) {
            String shortName = uName.stripName(player);
            event.setSignLine(NAME_LINE, shortName);
        }
    }
}
