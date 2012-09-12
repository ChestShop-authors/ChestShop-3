package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import static com.Acrobot.ChestShop.Config.Language.ACCESS_DENIED;
import static com.Acrobot.ChestShop.Config.Property.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY;

/**
 * @author Acrobot
 */
public class PlayerInventory implements Listener {
    @EventHandler
    public static void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (!Config.getBoolean(TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY)) {
            return;
        }

        HumanEntity entity = event.getPlayer();

        if (!(entity instanceof Player) || (!(event.getInventory().getHolder() instanceof Chest) && !(event.getInventory().getHolder() instanceof DoubleChest))) {
            return;
        }

        Player player = (Player) entity;
        Block chest = ((BlockState) event.getInventory().getHolder()).getBlock();

        if (!PlayerInteract.canOpenOtherShops(player) && !ChestShop.canAccess(player, chest)) {
            player.sendMessage(Config.getLocal(ACCESS_DENIED));
            event.setCancelled(true);
        }
    }
}
