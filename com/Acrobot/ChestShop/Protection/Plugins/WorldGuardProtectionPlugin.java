package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class WorldGuardProtectionPlugin implements Protection {
    private WorldGuardPlugin plugin;

    public WorldGuardProtectionPlugin(WorldGuardPlugin plugin) {
        this.plugin = plugin;
    }


    public boolean isProtected(Block block) {
        Vector pt = BukkitUtil.toVector(block);
        RegionManager mgr = plugin.getGlobalRegionManager().get(block.getWorld());
        ApplicableRegionSet set = mgr.getApplicableRegions(pt);

        return !set.allows(DefaultFlag.CHEST_ACCESS);
    }

    public boolean canAccess(Player player, Block block) {
        Vector pt = BukkitUtil.toVector(block);
        RegionManager mgr = plugin.getGlobalRegionManager().get(block.getWorld());
        ApplicableRegionSet set = mgr.getApplicableRegions(pt);
        LocalPlayer locPlayer = plugin.wrapPlayer(player);

        return plugin.getGlobalRegionManager().hasBypass(locPlayer, block.getWorld()) || set.canBuild(locPlayer) || set.allows(DefaultFlag.CHEST_ACCESS, locPlayer);
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
