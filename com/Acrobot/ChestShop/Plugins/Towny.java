package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Events.BuildPermissionEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlockOwner;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class Towny implements Listener {
    @EventHandler
    public static void canBuild(BuildPermissionEvent event) {
        Location chest = event.getChest();
        Location sign = event.getSign();

        if (isInWilderness(chest, sign) || !isInsideShopPlot(chest, sign)) {
            event.disallow();
            return;
        }

        boolean allow;

        if (Config.getBoolean(Property.TOWNY_SHOPS_FOR_OWNERS_ONLY)) {
            allow = isPlotOwner(event.getPlayer(), chest, sign);
        } else {
            allow = isResident(event.getPlayer(), chest, sign);
        }

        event.allow(allow);
    }

    private static boolean isResident(Player player, Location location) throws NotRegisteredException {
        return TownyUniverse.getTownBlock(location).getTown().hasResident(player.getName());
    }

    private static boolean isResident(Player player, Location... locations) {
        try {
            for (Location location : locations) {
                if (!isResident(player, location)) {
                    return false;
                }
            }
        } catch (NotRegisteredException exception) {
            return false;
        }

        return true;
    }

    private static boolean isPlotOwner(Player player, Location location) throws NotRegisteredException {
        TownBlockOwner owner = TownyUniverse.getDataSource().getResident(player.getName());
        return TownyUniverse.getTownBlock(location).isOwner(owner);
    }

    private static boolean isPlotOwner(Player player, Location... locations) {
        try {
            for (Location location : locations) {
                if (!isPlotOwner(player, location)) {
                    return false;
                }
            }
        } catch (NotRegisteredException exception) {
            return false;
        }

        return true;
    }

    private static boolean isInsideShopPlot(Location location) {
        return TownyUniverse.getTownBlock(location).getType() == TownBlockType.COMMERCIAL;
    }

    private static boolean isInsideShopPlot(Location... locations) {
        for (Location location : locations) {
            if (!isInsideShopPlot(location)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isInWilderness(Location location) {
        return TownyUniverse.isWilderness(location.getBlock());
    }

    private static boolean isInWilderness(Location... locations) {
        for (Location location : locations) {
            if (!isInWilderness(location)) {
                return false;
            }
        }

        return true;
    }
}
