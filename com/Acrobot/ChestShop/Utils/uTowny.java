package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


/**
 * @author Acrobot
 */
public class uTowny {
    public static boolean isInsideShopPlot(Location chestlocation, Location signLocation) {
        return TownyUniverse.getTownBlock(chestlocation).getType() == TownBlockType.COMMERCIAL && TownyUniverse.getTownBlock(signLocation).getType() == TownBlockType.COMMERCIAL;
    }

    public static boolean isPlotOwner(Player player, Location chestLocation, Location signLocation) {
        if (Config.getBoolean(Property.TOWNY_SHOPS_FOR_OWNERS_ONLY)) return isBlockOwner(player, chestLocation) && isBlockOwner(player, signLocation);
        return isResident(player, chestLocation) && isResident(player, signLocation);
    }

    public static boolean isInWilderness(Location chestLocation, Location signLocation) {
        return isInWilderness(chestLocation.getBlock()) || isInWilderness(signLocation.getBlock());
    }

    private static boolean isInWilderness(Block block) {
        return TownyUniverse.isWilderness(block);
    }

    public static boolean canBuild(Player player, Location chestLocation, Location signLocation) {
        return uSign.towny == null || !Config.getBoolean(Property.TOWNY_INTEGRATION) || (!isInWilderness(chestLocation, signLocation) && isInsideShopPlot(chestLocation, signLocation) && isPlotOwner(player, chestLocation, signLocation));
    }

    private static boolean isBlockOwner(Player player, Location location) {
        try {
            return TownyUniverse.getTownBlock(location).isOwner(TownyUniverse.getDataSource().getResident(player.getName()));
        } catch (NotRegisteredException ex) {
            return false;
        }
    }

    private static boolean isResident(Player p, Location l) {
        try {
            return TownyUniverse.getTownBlock(l).getTown().hasResident(p.getName());
        } catch (NotRegisteredException ex) {
            return false;
        }
    }
}
