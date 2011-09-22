package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyPermission;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * @author Acrobot
 */
public class uTowny {
    public static Towny towny;

    public static boolean isInsideShopPlot(Location chestlocation, Location signLocation) {
        return towny.getTownyUniverse().getTownBlock(chestlocation).getType() == TownBlockType.COMMERCIAL && towny.getTownyUniverse().getTownBlock(signLocation).getType() == TownBlockType.COMMERCIAL;
    }

    public static boolean isPlotOwner(Player player, Location chestLocation, Location signLocation){
        return isBlockOwner(player, chestLocation) && isBlockOwner(player, signLocation);
    }

    public static boolean canBuild(Player player, Location chestLocation, Location signLocation){
        return towny == null || !Config.getBoolean(Property.TOWNY_INTEGRATION) || (isInsideShopPlot(chestLocation, signLocation) && isPlotOwner(player, chestLocation, signLocation));
    }

    private static boolean isBlockOwner(Player player, Location location){
        try{
            return towny.getTownyUniverse().getTownBlock(location).isOwner(towny.getTownyUniverse().getResident(player.getName()));
        } catch (Exception ex){ return false; }
    }
}
