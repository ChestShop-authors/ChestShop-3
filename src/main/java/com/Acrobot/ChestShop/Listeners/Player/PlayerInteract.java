package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.Breeze.Utils.*;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Listeners.Economy.Plugins.VaultListener;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Plugins.ChestShop;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
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
import org.bukkit.event.block.SignChangeEvent;
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onInteract(PlayerInteractEvent event)
    {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (Properties.USE_BUILT_IN_PROTECTION && isChest(block)) {
            if (Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
                return;
            }

            if (!canOpenOtherShops(player) && !ChestShop.canAccess(player, block)) {
                player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
                event.setCancelled(true);
            }

            return;
        }

        if (!isSign(block) || player.getItemInHand().getType() == Material.SIGN) // Blocking accidental sign edition
            return;

        Sign sign = (Sign) block.getState();
        if (!ChestShopSign.isValid(sign)) {
            return;
        }

        boolean canAccess = ChestShopSign.canAccess(player, sign);
    
        if (Properties.ALLOW_AUTO_ITEM_FILL && ChatColor.stripColor(sign.getLine(ITEM_LINE)).equals(AUTOFILL_CODE)) {
            if (canAccess) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!MaterialUtil.isEmpty(item)) {
                    String itemCode = MaterialUtil.getSignName(item);
                    String[] lines = sign.getLines();
                    lines[ITEM_LINE] = itemCode;
    
                    SignChangeEvent changeEvent = new SignChangeEvent(block, player, lines);
                    com.Acrobot.ChestShop.ChestShop.callEvent(changeEvent);
                    if (!changeEvent.isCancelled()) {
                        for (byte i = 0; i < changeEvent.getLines().length; ++i) {
                            sign.setLine(i, changeEvent.getLine(i));
                        }
                        sign.update();
                    }
                } else {
                    player.sendMessage(Messages.prefix(Messages.NO_ITEM_IN_HAND));
                }
            } else {
                player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            }
            return;
        }
        
        if (canAccess) {
            if (!Properties.ALLOW_SIGN_CHEST_OPEN || player.isSneaking() || player.isInsideVehicle() || player.getGameMode() == GameMode.CREATIVE) {
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

        //Bukkit.getLogger().info("ChestShop - DEBUG - "+block.getWorld().getName()+": "+block.getLocation().getBlockX()+", "+block.getLocation().getBlockY()+", "+block.getLocation().getBlockZ());
        PreTransactionEvent pEvent = preparePreTransactionEvent(sign, player, action);
        if (pEvent == null)
            return;

        Bukkit.getPluginManager().callEvent(pEvent);
        if (pEvent.isCancelled())
            return;

        TransactionEvent tEvent = new TransactionEvent(pEvent, sign);
        Bukkit.getPluginManager().callEvent(tEvent);
    }

    private static PreTransactionEvent preparePreTransactionEvent(Sign sign, Player player, Action action) {
        String name = sign.getLine(NAME_LINE);
        String quantity = sign.getLine(QUANTITY_LINE);
        String prices = sign.getLine(PRICE_LINE);
        String material = sign.getLine(ITEM_LINE);

        Account account = NameManager.getLastAccountFromShortName(name);
        if (account == null) {
            player.sendMessage(Messages.prefix(Messages.PLAYER_NOT_FOUND));
            return null;
        }

        boolean adminShop = ChestShopSign.isAdminShop(sign);

        // check if player exists in economy
        if(!adminShop && !VaultListener.getProvider().hasAccount(account.getName())) {
            player.sendMessage(Messages.prefix(Messages.NO_ECONOMY_ACCOUNT));
            return null;
        }

        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        double price = (action == buy ? PriceUtil.getBuyPrice(prices) : PriceUtil.getSellPrice(prices));

        Chest chest = uBlock.findConnectedChest(sign);
        Inventory ownerInventory = (adminShop ? new AdminInventory() : chest != null ? chest.getInventory() : null);

        ItemStack item = MaterialUtil.getItem(material);
        if (item == null || !NumberUtil.isInteger(quantity)) {
            player.sendMessage(Messages.prefix(Messages.INVALID_SHOP_DETECTED));
            return null;
        }

        int amount = Integer.parseInt(quantity);

        if (amount < 1) {
            amount = 1;
        }

        if (Properties.SHIFT_SELLS_IN_STACKS && player.isSneaking() && price != PriceUtil.NO_PRICE && isAllowedForShift(action == buy)) {
            int newAmount = getStackAmount(item, ownerInventory, player, action);
            if (newAmount > 0) {
                price = (price / amount) * newAmount;
                amount = newAmount;
            }
        }

        item.setAmount(amount);

        ItemStack[] items = InventoryUtil.getItemsStacked(item);

        TransactionType transactionType = (action == buy ? BUY : SELL);
        return new PreTransactionEvent(ownerInventory, player.getInventory(), items, price, player, account, sign, transactionType);
    }

    private static boolean isAllowedForShift(boolean buyTransaction) {
        String allowed = Properties.SHIFT_ALLOWS;

        if (allowed.equalsIgnoreCase("ALL")) {
            return true;
        }

        return allowed.equalsIgnoreCase(buyTransaction ? "BUY" : "SELL");
    }

    private static int getStackAmount(ItemStack item, Inventory inventory, Player player, Action action) {
        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        Inventory checkedInventory = (action == buy ? inventory : player.getInventory());

        if (checkedInventory.containsAtLeast(item, InventoryUtil.getMaxStackSize(item))) {
            return InventoryUtil.getMaxStackSize(item);
        } else {
            return InventoryUtil.getAmount(item, checkedInventory);
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
