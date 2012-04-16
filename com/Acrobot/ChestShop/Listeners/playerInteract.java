package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Plugins.Default;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Shop.ShopManagement;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

/**
 * @author Acrobot
 */
public class playerInteract implements Listener {

    private static final HashMap<Player, Long> lastTransactionTime = new HashMap<Player, Long>(); //Last player's transaction
    public static int interval = 100;//Minimal interval between transactions

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (Config.getBoolean(Property.USE_BUILT_IN_PROTECTION) && block.getType() == Material.CHEST) {
            Default protection = new Default();
            if (!hasAdminPermissions(player) && (protection.isProtected(block) && !protection.canAccess(player, block))) {
                player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                event.setCancelled(true);
                return;
            }
        }

        if (!uSign.isSign(block)) return;
        Sign sign = (Sign) block.getState();

        if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.SIGN) return;
        if (!uSign.isValid(sign) || !enoughTimeHasPassed(player) || player.isSneaking()) return;

        if (Config.getBoolean(Property.IGNORE_CREATIVE_MODE) && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        lastTransactionTime.put(player, System.currentTimeMillis());

        if (action == Action.RIGHT_CLICK_BLOCK) event.setCancelled(true);

        if (uSign.canAccess(player, sign)) {
            if (!Config.getBoolean(Property.ALLOW_SIGN_CHEST_OPEN)) {
                return;
            }

            if (action != Action.LEFT_CLICK_BLOCK || !Config.getBoolean(Property.ALLOW_LEFT_CLICK_DESTROYING)) {
                showChestGUI(player, block);
            }
            return;
        }

        if (restrictedSign.isRestrictedShop(sign) && !restrictedSign.canAccess(sign, player)) {
            player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
            return;
        }

        Action buy = (Config.getBoolean(Property.REVERSE_BUTTONS) ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK);

        if (action == buy) ShopManagement.buy(sign, player);
        else ShopManagement.sell(sign, player);
    }

    private static boolean enoughTimeHasPassed(Player player) {
        return !lastTransactionTime.containsKey(player) || (System.currentTimeMillis() - lastTransactionTime.get(player)) >= interval;
    }

    private static boolean hasAdminPermissions(Player player) {
        return Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD);
    }

    private static void showChestGUI(Player player, Block block) {
        Chest chest = uBlock.findChest(block);
        if (chest == null) { //Sorry, no chest found
            player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
            return;
        }

        if (!hasAdminPermissions(player) && !Security.canAccess(player, block)) {
            return;
        }

        Inventory chestInv = chest.getInventory();
        player.openInventory(chestInv);
    }
}
