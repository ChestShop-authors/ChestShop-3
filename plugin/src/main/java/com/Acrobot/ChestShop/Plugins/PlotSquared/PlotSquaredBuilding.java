package com.Acrobot.ChestShop.Plugins.PlotSquared;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlotSquaredBuilding implements Listener {
    public PlotSquaredBuilding() {
        GlobalFlagContainer.getInstance().addFlag(AllowChestshopFlag.ALLOW_SHOP_FALSE);
    }

    @EventHandler(ignoreCancelled = true)
    public void canBuild(BuildPermissionEvent event) {
        org.bukkit.Location loc = event.getSign().getBlock().getLocation();
        Location location = Location.at(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        PlotArea plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(location);
        if (plotArea == null) {
            if (Properties.PLOTSQUARED_USE_FLAG || Properties.PLOTSQUARED_ONLY_IN_PLOT) {
                event.allow(false);
            }
            return;
        }
        Plot plot = plotArea.getPlot(location);

        if (plot != null && Properties.PLOTSQUARED_USE_FLAG) {
            event.allow(plot.getFlag(AllowChestshopFlag.class));
        } else if (Properties.PLOTSQUARED_ONLY_IN_PLOT) {
            event.allow(plot != null);
        }
    }

}
