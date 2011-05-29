package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Messaging.Message;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Shop.ShopManagement;
import com.Acrobot.ChestShop.Utils.Config;
import com.Acrobot.ChestShop.Utils.SearchForBlock;
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

        if (block.getType() == Material.CHEST) {
            if (Security.isProtected(block) && !Security.canAccess(player, block)) {
                Message.sendMsg(player, "ACCESS_DENIED");
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

        if(player.isSneaking()){
            return;
        }

        if(player.getName().startsWith(sign.getLine(0))){
            Chest chest1 = SearchForBlock.findChest(sign);
            Inventory inv1 = chest1.getInventory();
            IInventory iInv1 = ((CraftInventory) inv1).getInventory();

            Chest chest2 = SearchForBlock.findNeighbor(chest1);
            if(chest2 != null){
                Inventory inv2 = chest2.getInventory();
                IInventory iInv2 = ((CraftInventory) inv2).getInventory();
                IInventory largeChest = new InventoryLargeChest("Shop", iInv1, iInv2);
                ((CraftPlayer) player).getHandle().a(largeChest);
                return;
            } else if(chest1 != null){
                ((CraftPlayer) player).getHandle().a(iInv1);
                return;
            } else {
                player.sendMessage(Config.getLocal("NO_CHEST_DETECTED"));
                return;
            }
        }

        Action buy = (Config.getBoolean("reverse_buttons") ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK);

        if (action == buy) {
            ShopManagement.buy(sign, player);
        } else {
            ShopManagement.sell(sign, player);
        }
    }
}
