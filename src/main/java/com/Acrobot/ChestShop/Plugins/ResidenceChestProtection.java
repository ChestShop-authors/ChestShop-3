package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author TOOTHPlCK1
 */
public class ResidenceChestProtection implements Listener {

    @EventHandler
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();
        ClaimedResidence res = Residence.getResidenceManager().getByLoc(block.getLocation());

        if (res != null) {
            if (!res.getPermissions().playerHas(player.getName(), "container", false) && !Residence.isResAdminOn(player)) {
                //Doesn't have permissions to that chest.
                event.setResult(Event.Result.DENY);
            }
        }
    }
}