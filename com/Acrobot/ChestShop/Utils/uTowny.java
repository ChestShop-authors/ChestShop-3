package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * @author Acrobot
 */
public class uTowny {
    public static boolean isInsideShopPlot(Location chestlocation, Location signLocation) {
        return uSign.towny.getTownyUniverse().getTownBlock(chestlocation).getType() == TownBlockType.COMMERCIAL && uSign.towny.getTownyUniverse().getTownBlock(signLocation).getType() == TownBlockType.COMMERCIAL;
    }

    public static boolean isPlotOwner(Player player, Location chestLocation, Location signLocation) {
        return isBlockOwner(player, chestLocation) && isBlockOwner(player, signLocation);
    }

    public static boolean isNotInTheWilderness(Location chestLocation, Location signLocation) {
        return !uSign.towny.getTownyUniverse().isWilderness(chestLocation.getBlock()) && !uSign.towny.getTownyUniverse().isWilderness(signLocation.getBlock());
    }

    public static boolean canBuild(Player player, Location chestLocation, Location signLocation) {
        return uSign.towny == null || !Config.getBoolean(Property.TOWNY_INTEGRATION) || (isNotInTheWilderness(chestLocation, signLocation) && isInsideShopPlot(chestLocation, signLocation) && isPlotOwner(player, chestLocation, signLocation));
    }

    private static boolean isBlockOwner(Player player, Location location) {
        try {
            return uSign.towny.getTownyUniverse().getTownBlock(location).isOwner(uSign.towny.getTownyUniverse().getResident(player.getName()));
        } catch (NotRegisteredException ex) { return false; }
    }
}
