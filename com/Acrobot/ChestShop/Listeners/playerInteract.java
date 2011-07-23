package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Default;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Shop.ShopManagement;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uSign;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryLargeChest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.HashMap;

/**
 * @author Acrobot
 */
public class playerInteract extends PlayerListener {

    private static final HashMap<Player, Long> lastTransactionTime = new HashMap<Player, Long>();
    private static final int interval = 100;

    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (Config.getBoolean(Property.USE_BUILT_IN_PROTECTION) && block.getType() == Material.CHEST) {
            Default protection = new Default();
            if (!Permission.has(player, Permission.ADMIN) && !Permission.has(player, Permission.MOD) && (protection.isProtected(block) && !protection.canAccess(player, block))) {
                player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                event.setCancelled(true);
                return;
            }
        }

        if (!uSign.isSign(block)) return;

        Sign sign = (Sign) block.getState();

        if (!uSign.isValid(sign) || lastTransactionTime.containsKey(player) && (System.currentTimeMillis() - lastTransactionTime.get(player)) < interval || player.isSneaking()) return;

        lastTransactionTime.put(player, System.currentTimeMillis());

        String playerName = player.getName();

        if (playerName.equals(sign.getLine(0)) || uLongName.stripName(playerName).equals(sign.getLine(0))) {
            Chest chest1 = uBlock.findChest(sign);
            if (chest1 == null) {
                player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
                return;
            }

            IInventory inventory = ((CraftInventory) chest1.getInventory()).getInventory();
            Chest chest2 = uBlock.findNeighbor(chest1);

            if (chest2 != null) {
                IInventory iInv2 = ((CraftInventory) chest2.getInventory()).getInventory();
                inventory = new InventoryLargeChest(player.getName() + "'s Shop", inventory, iInv2);
            }

            ((CraftPlayer) player).getHandle().a(inventory);
            return;
        }

        if (restrictedSign.isRestrictedShop(sign) && !restrictedSign.canAccess(sign, player)) {
            player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
            return;
        }

        Action buy = (Config.getBoolean(Property.REVERSE_BUTTONS) ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK);

        if (action == buy) {
            ShopManagement.buy(sign, player);
        } else {
            ShopManagement.sell(sign, player);
        }
    }
}
