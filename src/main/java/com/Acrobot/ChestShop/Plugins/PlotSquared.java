package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlotSquared implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void canBuild(BuildPermissionEvent event) {
        Block block = event.getSign().getBlock();

        Location location = com.plotsquared.bukkit.util.BukkitUtil.adapt(block.getLocation());
        Plot plot = location.getPlot();

        if(plot == null) {
            event.allow(false);
            return;
        }

        if(!plot.getFlag(ChestshopAllowShopFlag.class)) {
            event.allow(false);
        }
    }
}

