package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Security;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class LightweightChestProtection implements Listener {
    private LWC lwc;

    public LightweightChestProtection() {
        this.lwc = LWC.getInstance();
    }

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        Player player = event.getPlayer();
        Sign sign = event.getSign();
        Container connectedContainer = event.getContainer();

        if (Properties.PROTECT_SIGN_WITH_LWC) {
            if (!Security.protect(player, sign.getBlock(), event.getOwnerAccount() != null ? event.getOwnerAccount().getUuid() : player.getUniqueId())) {
                player.sendMessage(Messages.prefix(Messages.NOT_ENOUGH_PROTECTIONS));
            }
        }

        if (Properties.PROTECT_CHEST_WITH_LWC && connectedContainer != null
                && Security.protect(player, connectedContainer.getBlock(), event.getOwnerAccount() != null ? event.getOwnerAccount().getUuid() : player.getUniqueId())) {
            player.sendMessage(Messages.prefix(Messages.PROTECTED_SHOP));
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

        if (!lwc.canAccessProtection(player, protection) || protection.getType() == Protection.Type.DONATION) {
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onBlockProtect(ProtectBlockEvent event) {
        if (event.isProtected()) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String worldName = block.getWorld().getName();

        Protection existingProtection = lwc.getPhysicalDatabase().loadProtection(worldName, x, y, z);

        if (existingProtection != null) {
            event.setProtected(true);
            return;
        }

        LWCProtectionRegisterEvent protectionEvent = new LWCProtectionRegisterEvent(player, block);
        lwc.getModuleLoader().dispatchEvent(protectionEvent);

        if (protectionEvent.isCancelled()) {
            return;
        }

        Protection protection = null;
        try {
            protection = lwc.getPhysicalDatabase().registerProtection(block.getType(), Protection.Type.PRIVATE, worldName, event.getProtectionOwner().toString(), "", x, y, z);
        } catch (LinkageError e) {
            try {
                int blockId = com.griefcraft.cache.BlockCache.getInstance().getBlockId(block);
                if (blockId < 0) {
                    return;
                }
                protection = lwc.getPhysicalDatabase().registerProtection(blockId, Protection.Type.PRIVATE, worldName, event.getProtectionOwner().toString(), "", x, y, z);
            } catch (LinkageError e2) {
                ChestShop.getBukkitLogger().warning(
                        "Incompatible LWC version installed! (" + lwc.getPlugin().getName() + " v" + lwc.getVersion()  + ") \n" +
                                "Material method error: " + e.getMessage() + "\n" +
                                "Block cache/type id error: " + e2.getMessage()
                );
            }
        }

        if (protection != null) {
            event.setProtected(true);
        }
    }

    @EventHandler
    public void onShopRemove(ShopDestroyedEvent event) {
        Protection signProtection = lwc.findProtection(event.getSign().getBlock());

        if (signProtection != null) {
            signProtection.remove();
        }

        if (event.getContainer() == null || !Properties.REMOVE_LWC_PROTECTION_AUTOMATICALLY) {
            return;
        }

        Protection chestProtection = lwc.findProtection(event.getContainer().getBlock());

        if (chestProtection != null) {
            chestProtection.remove();
        }
    }
}
