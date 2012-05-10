package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.ProtectionCheckEvent;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.modules.limits.LimitsV2;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class LightweightChestProtection implements Listener {
    private LWC lwc;
    private LimitsV2 limitsModule;

    public LightweightChestProtection(LWC lwc) {
        this.lwc = lwc;
        limitsModule = new LimitsV2();
    }

    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        Protection protection = lwc.findProtection(block);

        if (protection == null) {
            return;
        }

        if (!lwc.canAccessProtection(player, protection)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onBlockProtect(ProtectBlockEvent event) {
        if (event.isProtected()) {
            return;
        }

        Block block = event.getBlock();
        Player player = Bukkit.getPlayer(event.getName());

        if (player == null || limitsModule.hasReachedLimit(player, block.getType())) {
            return;
        }

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String worldName = block.getWorld().getName();

        Protection protection = lwc.getPhysicalDatabase().registerProtection(block.getTypeId(), Protection.Type.PRIVATE, worldName, event.getName(), "", x, y, z);

        if (protection != null) {
            event.setProtected(true);
        }
    }
}
