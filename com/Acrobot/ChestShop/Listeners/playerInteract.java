package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Security;
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
public class PlayerInteract implements Listener {
    private static final HashMap<Player, Long> timeOfTheLatestSignClick = new HashMap<Player, Long>();
    public int transactionBlockInterval = 100;

    public PlayerInteract(int transactionInterval) {
        this.transactionBlockInterval = transactionInterval;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!playerClickedBlock(action)) {
            return;
        }

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (Config.getBoolean(Property.USE_BUILT_IN_PROTECTION) && block.getType() == Material.CHEST) {
            if (!hasAdminPermissions(player) && !ChestShop.canAccess(player, block)) {
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

        timeOfTheLatestSignClick.put(player, System.currentTimeMillis());

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

    private boolean enoughTimeHasPassed(Player player) {
        return !timeOfTheLatestSignClick.containsKey(player) || (System.currentTimeMillis() - timeOfTheLatestSignClick.get(player)) >= transactionBlockInterval;
    }

    private static boolean playerClickedBlock(Action action) {
        return action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK;
    }


    private static boolean hasAdminPermissions(Player player) {
        return Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD);
    }

    private static void showChestGUI(Player player, Block block) {
        Chest chest = uBlock.findConnectedChest(block);
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
