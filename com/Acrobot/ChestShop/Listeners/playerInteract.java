package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Default;
import com.Acrobot.ChestShop.Restrictions.RestrictedSign;
import com.Acrobot.ChestShop.Shop.ShopManagement;
import com.Acrobot.ChestShop.Utils.BlockSearch;
import com.Acrobot.ChestShop.Utils.SignUtil;
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
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

/**
 * @author Acrobot
 */
public class playerInteract extends PlayerListener {

    private HashMap<Player, Long> time = new HashMap<Player, Long>();
    public static int interval = 100;

    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (Config.getBoolean(Property.USE_BUILT_IN_PROTECTION) && block.getType() == Material.CHEST) {
            Default defProtection = new Default();
            if (!Permission.has(player, Permission.ADMIN) && (defProtection.isProtected(block) && !defProtection.canAccess(player, block))) {
                player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                event.setCancelled(true);
                return;
            }
        }

        if (!SignUtil.isSign(block)) {
            return;
        }
        Sign sign = (Sign) block.getState();
        if (!SignUtil.isValid(sign)) {
            return;
        }

        if (time.containsKey(player) && (System.currentTimeMillis() - time.get(player)) < interval) {
            return;
        }

        time.put(player, System.currentTimeMillis());

        if (player.isSneaking()) {
            return;
        }

        if (player.getName().equals(sign.getLine(0))) {
            Chest chest1 = BlockSearch.findChest(sign);
            if (chest1 == null) {
                player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
                return;
            }

            Inventory inv1 = chest1.getInventory();
            IInventory iInv1 = ((CraftInventory) inv1).getInventory();

            Chest chest2 = BlockSearch.findNeighbor(chest1);

            if (chest2 != null) {
                Inventory inv2 = chest2.getInventory();
                IInventory iInv2 = ((CraftInventory) inv2).getInventory();
                IInventory largeChest = new InventoryLargeChest(player.getName() + "'s Shop", iInv1, iInv2);
                ((CraftPlayer) player).getHandle().a(largeChest);
            } else {
                ((CraftPlayer) player).getHandle().a(iInv1);
            }
            return;
        }

        if(RestrictedSign.isRestricted(sign)){
            if(!RestrictedSign.canAccess(sign, player)){
                player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                return;
            }
        }

        Action buy = (Config.getBoolean(Property.REVERSE_BUTTONS) ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK);

        if (action == buy) {
            ShopManagement.buy(sign, player);
        } else {
            ShopManagement.sell(sign, player);
        }
    }
}
