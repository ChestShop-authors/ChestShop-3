package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author TOOTHPlCK1
 * @author Andrzej Pomirski
 */
public class ResidenceChestProtection implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY && !Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();
        ClaimedResidence residence = ResidenceApi.getResidenceManager().getByLoc(block.getLocation());

        if (residence != null) {
            if (!residence.getPermissions().playerHas(player, Flags.container, true) && !Residence.getInstance().isResAdminOn(player)) {
                //Doesn't have permissions to that chest.
                event.setResult(Event.Result.DENY);
            }
        }
    }
}