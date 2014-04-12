package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
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

        if (name.isEmpty() || (!NameManager.canUseName(player, name) && !Permission.has(player, Permission.ADMIN))) {
            String shortName = NameUtil.stripUsername(NameManager.getUsername(player.getUniqueId()));
            event.setSignLine(NAME_LINE, shortName);
        }
    }
}
