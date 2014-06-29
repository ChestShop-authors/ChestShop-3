package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import net.t00thpick1.residence.Residence;
import net.t00thpick1.residence.api.ResidenceAPI;
import net.t00thpick1.residence.api.areas.ResidenceArea;
import net.t00thpick1.residence.api.flags.FlagManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author TOOTHPlCK1
 * @author Andrzej Pomirski
 */
public class ResidenceChestProtection implements Listener {

    @EventHandler
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();
        ResidenceArea area = ResidenceAPI.getResidenceManager().getByLocation(block.getLocation());

        if (area != null) {
            if (!area.allowAction(player.getName(), FlagManager.CONTAINER) && !Residence.getInstance().isAdminMode(player)) {
                //Doesn't have permissions to that chest.
                event.setResult(Event.Result.DENY);
            }
        }
    }
}