package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.modules.limits.LimitsModule;
import com.griefcraft.modules.limits.LimitsV2;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCProtectionRegistrationPostEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.OTHER_BREAK;

/**
 * @author Acrobot
 */
public class LightweightChestProtection implements Listener {
    private final LWC lwc;
    private final LimitsModule limitsModule;
    private final LimitsV2 limitsV2;

    public LightweightChestProtection() {
        this.lwc = LWC.getInstance();
        this.limitsModule = (LimitsModule) lwc.getModuleLoader().getModule(LimitsModule.class);
        this.limitsV2 = (LimitsV2) lwc.getModuleLoader().getModule(LimitsV2.class);
        try {
            if (Properties.PROTECT_SIGN_WITH_LWC)
                Protection.Type.valueOf(Properties.LWC_SIGN_PROTECTION_TYPE.name());
            if (Properties.PROTECT_CHEST_WITH_LWC)
                Protection.Type.valueOf(Properties.LWC_CHEST_PROTECTION_TYPE.name());
        } catch (IllegalArgumentException e) {
            ChestShop.getBukkitLogger().warning("Your installed LWC version doesn't seem to support the configured protection type! " + e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreShopCreation(PreShopCreationEvent event) {
        if (Properties.LWC_LIMITS_BLOCK_CREATION) {
            if (Properties.PROTECT_SIGN_WITH_LWC) {
                if (isAtLimit(event.getPlayer(), event.getSign())) {
                    event.setOutcome(OTHER_BREAK);
                    return;
                }
            }

            if (Properties.PROTECT_CHEST_WITH_LWC) {
                Container container = uBlock.findConnectedContainer(event.getSign());
                if (container != null && isAtLimit(event.getPlayer(), container)) {
                    event.setOutcome(OTHER_BREAK);
                    return;
                }
            }
        }
    }

    private boolean isAtLimit(Player player, BlockState blockState) {
        LWCProtectionRegisterEvent protectionEvent = new LWCProtectionRegisterEvent(player, blockState.getBlock());
        limitsModule.onRegisterProtection(protectionEvent);
        limitsV2.onRegisterProtection(protectionEvent);
        return protectionEvent.isCancelled();
    }

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        Player player = event.getPlayer();
        Sign sign = event.getSign();
        Container connectedContainer = event.getContainer();

        Messages.Message message = null;
        if (Properties.PROTECT_SIGN_WITH_LWC) {
            if (Security.protect(player, sign.getBlock(), event.getOwnerAccount() != null ? event.getOwnerAccount().getUuid() : player.getUniqueId(), Properties.LWC_SIGN_PROTECTION_TYPE)) {
                message = Messages.PROTECTED_SHOP_SIGN;
            } else {
                message = Messages.NOT_ENOUGH_PROTECTIONS;
            }
        }

        if (Properties.PROTECT_CHEST_WITH_LWC && connectedContainer != null) {
            if (Security.protect(player, connectedContainer.getBlock(), event.getOwnerAccount() != null ? event.getOwnerAccount().getUuid() : player.getUniqueId(), Properties.LWC_CHEST_PROTECTION_TYPE)) {
                message = Messages.PROTECTED_SHOP;
            } else if (message == null) {
                message = Messages.NOT_ENOUGH_PROTECTIONS;
            }
        }

        if (message != null) {
            message.sendWithPrefix(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY && !Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
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
        } else if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
            event.setResult(Event.Result.ALLOW);
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

        Protection.Type type = Protection.Type.PRIVATE;
        switch (event.getType()) {
            case PUBLIC:
                type = Protection.Type.PUBLIC;
                break;
            case DONATION:
                type = Protection.Type.DONATION;
                break;
            case DISPLAY:
                try {
                    type = Protection.Type.valueOf("DISPLAY");
                } catch (IllegalArgumentException ignored) {}
                break;
        }

        Protection protection = null;
        try {
            int blockId = com.griefcraft.cache.BlockCache.getInstance().getBlockId(block);
            if (blockId < 0) {
                return;
            }
            protection = lwc.getPhysicalDatabase().registerProtection(blockId, type, worldName, event.getProtectionOwner().toString(), "", x, y, z);
        } catch (LinkageError e) {
            ChestShop.getBukkitLogger().warning(
                    "Incompatible LWC version installed! (" + lwc.getPlugin().getName() + " v" + lwc.getVersion()  + ") \n" +
                            "Block cache/type id error: " + e.getMessage()
            );
        }

        if (protection != null) {
            event.setProtected(true);
            protection.removeCache();
            lwc.getProtectionCache().addProtection(protection);
            lwc.getModuleLoader().dispatchEvent(new LWCProtectionRegistrationPostEvent(protection));
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
