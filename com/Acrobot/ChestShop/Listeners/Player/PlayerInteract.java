package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

/**
 * @author Acrobot
 */
public class PlayerInteract implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (Properties.USE_BUILT_IN_PROTECTION && clickedBlock.getType() == Material.CHEST) {
            if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
                return;
            }

            if (!canOpenOtherShops(player) && !ChestShop.canAccess(player, clickedBlock)) {
                player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
                event.setCancelled(true);
            }

            return; //The else doesn't matter, because the clicked block is a chest
        }

        if (!BlockUtil.isSign(clickedBlock)) return;
        Sign sign = (Sign) clickedBlock.getState();

        if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.SIGN) return;
        if (!ChestShopSign.isValid(sign) || (ChestShopSign.canAccess(player, sign) && player.isSneaking())) return;

        if (action == RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }

        if (ChestShopSign.canAccess(player, sign)) {
            if (!Properties.ALLOW_SIGN_CHEST_OPEN) {
                return;
            }

            if (action != LEFT_CLICK_BLOCK || !Properties.ALLOW_LEFT_CLICK_DESTROYING) {
                showChestGUI(player, clickedBlock);
            }
            return;
        }

        PreTransactionEvent pEvent = preparePreTransactionEvent(sign, player, action);
        Bukkit.getPluginManager().callEvent(pEvent);

        if (pEvent.isCancelled()) {
            return;
        }

        TransactionEvent tEvent = new TransactionEvent(pEvent, sign);
        Bukkit.getPluginManager().callEvent(tEvent);
    }

    private static PreTransactionEvent preparePreTransactionEvent(Sign sign, Player player, Action action) {
        String ownerName = uName.getName(sign.getLine(NAME_LINE));
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);

        String priceLine = sign.getLine(PRICE_LINE);

        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        double price = (action == buy ? PriceUtil.getBuyPrice(priceLine) : PriceUtil.getSellPrice(priceLine));

        Chest chest = uBlock.findConnectedChest(sign);
        Inventory ownerInventory = (ChestShopSign.isAdminShop(sign) ? new AdminInventory() : chest != null ? chest.getInventory() : null);

        ItemStack item = MaterialUtil.getItem(sign.getLine(ITEM_LINE));

        int amount = Integer.parseInt(sign.getLine(QUANTITY_LINE));

        if (amount < 1) {
            amount = 1;
        }

        if (Properties.SHIFT_SELLS_EVERYTHING && player.isSneaking() && price != PriceUtil.NO_PRICE) {
            int newAmount = getItemAmount(item, ownerInventory, player, action);
            if (newAmount > 0) {
                price = (price / amount) * newAmount;
                amount = newAmount;
            }
        }

        item.setAmount(amount);

        ItemStack[] items = {item};

        TransactionType transactionType = (action == buy ? BUY : SELL);
        return new PreTransactionEvent(ownerInventory, player.getInventory(), items, price, player, owner, sign, transactionType);
    }

    private static int getItemAmount(ItemStack item, Inventory inventory, Player player, Action action) {
        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;

        if (action == buy) {
            return InventoryUtil.getAmount(item, inventory);
        } else {
            return InventoryUtil.getAmount(item, player.getInventory());
        }
    }

    public static boolean canOpenOtherShops(Player player) {
        return Permission.has(player, Permission.ADMIN) || Permission.has(player, Permission.MOD);
    }

    private static void showChestGUI(Player player, Block block) {
        Chest chest = uBlock.findConnectedChest(block);

        if (chest == null) {
            player.sendMessage(Messages.prefix(Messages.NO_CHEST_DETECTED));
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
