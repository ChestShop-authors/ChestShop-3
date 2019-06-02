package com.Acrobot.ChestShop.Plugins;

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Material;
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
    /**
     * If both the server and LWC support block IDs
     */
    private boolean id_supported = false;
    /**
     * If the LWC version being used supports Materials
     */
    private boolean material_supported = false;
    Method protect_by_id = null;

    public LightweightChestProtection() {
        this.lwc = LWC.getInstance();
        // cheap hack
        Class db = lwc.getPhysicalDatabase().getClass();
        try {
            Material.AIR.getId();
            protect_by_id = db.getDeclaredMethod("registerProtection", int.class, Protection.Type.class, String.class, String.class, String.class, int.class, int.class, int.class);
            id_supported = true;
        } catch (Throwable ignore) {}
        try {
            db.getDeclaredMethod("registerProtection", Material.class, Protection.Type.class, String.class, String.class, String.class, int.class, int.class, int.class);
            material_supported = true;
        } catch (NoSuchMethodException | SecurityException ignore) {}
    }

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        Player player = event.getPlayer();
        Sign sign = event.getSign();
        Container connectedContainer = event.getContainer();

        if (Properties.PROTECT_SIGN_WITH_LWC) {
            if (!Security.protect(player, sign.getBlock())) {
                player.sendMessage(Messages.prefix(Messages.NOT_ENOUGH_PROTECTIONS));
            }
        }

        if (Properties.PROTECT_CHEST_WITH_LWC && connectedContainer != null && Security.protect(player, connectedContainer.getBlock())) {
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
        // funny bit: some versions of LWC being used on older servers don't support passing Material
        if(material_supported) {
            protection = lwc.getPhysicalDatabase().registerProtection(block.getType(), Protection.Type.PRIVATE, worldName, player.getUniqueId().toString(), "", x, y, z);
        } else if(id_supported) {
            try {
                // if we're on an older server that supports ids, use that.
                protection = (Protection) protect_by_id.invoke(lwc.getPhysicalDatabase(), block.getType().getId(), Protection.Type.PRIVATE, worldName, player.getUniqueId().toString(), "", x, y, z);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                // something went wrong
                id_supported = false;
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
