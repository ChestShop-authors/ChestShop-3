package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class WorldGuardProtection implements Listener {
    private WorldGuardPlugin worldGuard;

    public WorldGuardProtection(WorldGuardPlugin worldGuard) {
        this.worldGuard = worldGuard;
    }

    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        Vector blockPos = BukkitUtil.toVector(block);
        RegionManager manager = worldGuard.getRegionManager(block.getWorld());
        ApplicableRegionSet set = manager.getApplicableRegions(blockPos);

        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

        if (!canAccess(localPlayer, block, set)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private boolean canAccess(LocalPlayer player, Block block, ApplicableRegionSet set) {
        return worldGuard.getGlobalRegionManager().hasBypass(player, block.getWorld()) || set.canBuild(player) || set.allows(DefaultFlag.CHEST_ACCESS, player);
    }
}
