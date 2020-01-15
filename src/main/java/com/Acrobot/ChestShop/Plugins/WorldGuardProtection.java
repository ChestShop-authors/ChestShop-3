package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * @author Acrobot
 */
public class WorldGuardProtection implements Listener {
    private WorldGuardPlugin worldGuard;
    private WorldGuardPlatform worldGuardPlatform;

    public WorldGuardProtection(Plugin plugin) {
        this.worldGuard =(WorldGuardPlugin) plugin;
        this.worldGuardPlatform = WorldGuard.getInstance().getPlatform();
    }

    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        BlockVector3 blockPos = BukkitAdapter.adapt(block.getLocation()).toVector().toBlockPoint();
        RegionManager manager = worldGuardPlatform.getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
        if (manager == null) {
            return;
        }
        ApplicableRegionSet set = manager.getApplicableRegions(blockPos);

        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

        if (!canAccess(localPlayer, block, set)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private boolean canAccess(LocalPlayer player, Block block, ApplicableRegionSet set) {
        return new RegionPermissionModel(player).mayIgnoreRegionProtection(BukkitAdapter.adapt(block.getWorld()))
                || set.testState(player, Flags.BUILD)
                || set.testState(player, Flags.CHEST_ACCESS);
    }
}
