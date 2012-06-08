package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Security;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.modules.limits.LimitsV2;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Config.Language.NOT_ENOUGH_PROTECTIONS;
import static com.Acrobot.ChestShop.Config.Language.PROTECTED_SHOP;
import static com.Acrobot.ChestShop.Config.Property.PROTECT_CHEST_WITH_LWC;
import static com.Acrobot.ChestShop.Config.Property.PROTECT_SIGN_WITH_LWC;

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
    public static void onShopCreation(ShopCreatedEvent event) {
        Player player = event.getPlayer();
        Sign sign = event.getSign();
        Chest connectedChest = event.getChest();

        if (Config.getBoolean(PROTECT_SIGN_WITH_LWC)) {
            if (!Security.protect(player.getName(), sign.getBlock())) {
                player.sendMessage(Config.getLocal(NOT_ENOUGH_PROTECTIONS));
            }
        }

        if (Config.getBoolean(PROTECT_CHEST_WITH_LWC) && connectedChest != null && Security.protect(player.getName(), connectedChest.getBlock())) {
            player.sendMessage(Config.getLocal(PROTECTED_SHOP));
        }
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
