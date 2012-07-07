package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Shop.Shop;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Signs.RestrictedSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.Map;
import java.util.UUID;

import static com.Acrobot.ChestShop.Config.Language.ACCESS_DENIED;
import static com.Acrobot.ChestShop.Config.Property.*;
import static com.Acrobot.ChestShop.Events.TransactionEvent.Type.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.Type.SELL;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

/**
 * @author Acrobot
 */
public class PlayerInteract implements Listener {
    private static final Map<UUID, Long> TIME_OF_THE_LATEST_CLICK = new HashMap<UUID, Long>();
    private static final String ITEM_NOT_RECOGNISED = ChatColor.RED + "[Shop] The item is not recognised!";

    private final int transactionBlockInterval;

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

        if (Config.getBoolean(USE_BUILT_IN_PROTECTION) && block.getType() == Material.CHEST) {
            if (!canOpenOtherShops(player) && !ChestShop.canAccess(player, block)) {
                player.sendMessage(Config.getLocal(ACCESS_DENIED));
                event.setCancelled(true);
                return;
            }
        }

        if (!BlockUtil.isSign(block)) return;
        Sign sign = (Sign) block.getState();

        if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.SIGN) return;
        if (!ChestShopSign.isValid(sign) || !enoughTimeHasPassed(player) || player.isSneaking()) return;

        if (Config.getBoolean(IGNORE_CREATIVE_MODE) && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        TIME_OF_THE_LATEST_CLICK.put(player.getUniqueId(), System.currentTimeMillis());

        if (action == RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }

        if (ChestShopSign.canAccess(player, sign)) {
            if (!Config.getBoolean(ALLOW_SIGN_CHEST_OPEN)) {
                return;
            }

            if (action != LEFT_CLICK_BLOCK || !Config.getBoolean(ALLOW_LEFT_CLICK_DESTROYING)) {
                showChestGUI(player, block);
            }
            return;
        }

        if (RestrictedSign.isRestrictedShop(sign) && !RestrictedSign.canAccess(sign, player)) {
            player.sendMessage(Config.getLocal(ACCESS_DENIED));
            return;
        }

        Action buy = (Config.getBoolean(REVERSE_BUTTONS) ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK);

        Shop shop = Shop.getShopFromSign(sign);

        if (shop == null) {
            player.sendMessage(ITEM_NOT_RECOGNISED);
            return;
        }

        PreTransactionEvent pEvent = new PreTransactionEvent(shop, player, action == buy ? BUY : SELL);
        Bukkit.getPluginManager().callEvent(pEvent);

        if (pEvent.isCancelled()) {
            return;
        }

        if (action == buy) {
            shop.sellToPlayer(player);
        } else {
            shop.buyFromPlayer(player);
        }
    }

    private boolean enoughTimeHasPassed(Player player) {
        UUID uniqueID = player.getUniqueId();

        return !TIME_OF_THE_LATEST_CLICK.containsKey(uniqueID) || (System.currentTimeMillis() - TIME_OF_THE_LATEST_CLICK.get(uniqueID)) >= transactionBlockInterval;
    }

    private static boolean playerClickedBlock(Action action) {
        return action == LEFT_CLICK_BLOCK || action == RIGHT_CLICK_BLOCK;
    }


    private static boolean canOpenOtherShops(Player player) {
        return Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD);
    }

    private static void showChestGUI(Player player, Block block) {
        Chest chest = uBlock.findConnectedChest(block);

        if (chest == null) {
            player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
            return;
        }

        if (!canOpenOtherShops(player) && !Security.canAccess(player, block)) {
            return;
        }

        if (chest.getBlock().getType() != Material.CHEST) {
            return; //To prevent people from breaking the chest and instantly clicking the sign
        }

        Inventory chestInv = chest.getInventory();
        player.openInventory(chestInv);
    }
}
