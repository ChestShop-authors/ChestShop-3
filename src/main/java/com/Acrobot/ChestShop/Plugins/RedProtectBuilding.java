package com.Acrobot.ChestShop.Plugins;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * @author Acrobot
 */
public class RedProtectBuilding implements Listener {
    private RedProtect redProtect;

    public RedProtectBuilding(Plugin plugin) {
        this.redProtect = (RedProtect) plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void canBuild(BuildPermissionEvent event) {
        Region region = redProtect.getAPI().getRegion(event.getSign());
        event.allow(region != null && region.canBuild(event.getPlayer()));
    }
}
