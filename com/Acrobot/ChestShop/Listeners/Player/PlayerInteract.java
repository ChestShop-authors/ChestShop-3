package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.Breeze.Utils.*;
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
import org.bukkit.GameMode;
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

import static com.Acrobot.Breeze.Utils.BlockUtil.isChest;
import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;
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
    public static void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (isChest(block) && Properties.USE_BUILT_IN_PROTECTION) {
            if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
                return;
            }

            if (!canOpenOtherShops(player) && !ChestShop.canAccess(player, block)) {
                player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
                event.setCancelled(true);
            }

            return;
        }

        if (!isSign(block) || player.getItemInHand().getType() == Material.SIGN) { // Blocking accidental sign edition
            return;
        }

        Sign sign = (Sign) block.getState();

        if (!ChestShopSign.isValid(sign)) {
            return;
        }

        if (ChestShopSign.canAccess(player, sign)) {
            if (!Properties.ALLOW_SIGN_CHEST_OPEN || player.isSneaking() || player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            if (!Properties.ALLOW_LEFT_CLICK_DESTROYING || action != LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
                showChestGUI(player, block);
            }

            return;
        }

        if (action == RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }

        PreTransactionEvent pEvent = preparePreTransactionEvent(sign, player, action);

        if (pEvent == null) {
            return;
        }

        Bukkit.getPluginManager().callEvent(pEvent);

        if (pEvent.isCancelled()) {
            return;
        }

        TransactionEvent tEvent = new TransactionEvent(pEvent, sign);
        Bukkit.getPluginManager().callEvent(tEvent);
    }

    private static PreTransactionEvent preparePreTransactionEvent(Sign sign, Player player, Action action) {
        String name = sign.getLine(NAME_LINE);
        String quantity = sign.getLine(QUANTITY_LINE);
        String prices = sign.getLine(PRICE_LINE);
        String material = sign.getLine(ITEM_LINE);

        String ownerName = uName.getName(name);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);

        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        double price = (action == buy ? PriceUtil.getBuyPrice(prices) : PriceUtil.getSellPrice(prices));

        Chest chest = uBlock.findConnectedChest(sign);
        Inventory ownerInventory = (ChestShopSign.isAdminShop(sign) ? new AdminInventory() : chest != null ? chest.getInventory() : null);

        ItemStack item = MaterialUtil.getItem(material);

        if (item == null || !NumberUtil.isInteger(quantity)) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return null;
        }

        int amount = Integer.parseInt(quantity);

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

    private static void showChestGUI(Player player, Block signBlock) {
        Chest chest = uBlock.findConnectedChest(signBlock);

        if (chest == null) {
            player.sendMessage(Messages.prefix(Messages.NO_CHEST_DETECTED));
            return;
        }

        if (!canOpenOtherShops(player) && !Security.canAccess(player, signBlock)) {
            return;
        }

        BlockUtil.openBlockGUI(chest, player);
    }
}
