package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
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
        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
        Location location = BukkitAdapter.adapt(block.getLocation());

        if (!canAccess(localPlayer, block, location)) {
            event.setResult(Event.Result.DENY);
            return;
        }

        RegionManager manager = worldGuardPlatform.getRegionContainer().get((World) location.getExtent());
        if (manager == null) {
            return;
        }
        ApplicableRegionSet set = manager.getApplicableRegions(location.toVector().toBlockPoint());

        StateFlag flag = Flags.CHEST_ACCESS;
        if (BlockUtil.isSign(block)) {
            flag = Flags.USE;
        }

        if (!canAccess(localPlayer, (World) location.getExtent(), set, flag)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private boolean canAccess(LocalPlayer player, Block block, Location location) {
        BukkitWorldConfiguration wcfg = (BukkitWorldConfiguration) worldGuardPlatform.getGlobalStateManager().get((World) location.getExtent());
        return !wcfg.signChestProtection
                || !wcfg.getChestProtection().isChest(BukkitAdapter.asBlockType(block.getType()))
                || !wcfg.getChestProtection().isProtected(location, player);
    }

    private boolean canAccess(LocalPlayer player, World world, ApplicableRegionSet set, StateFlag flag) {
        return new RegionPermissionModel(player).mayIgnoreRegionProtection(world)
                || set.testState(player, Flags.BUILD)
                || set.testState(player, flag);
    }
}
