package com.Acrobot.ChestShop.Listeners.Player;

import com.Acrobot.Breeze.Utils.*;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Commands.AccessToggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Events.Economy.AccountCheckEvent;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.ShopInfoEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.logging.Level;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getState;
import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.SELL;
import static com.Acrobot.ChestShop.Permission.OTHER_NAME_CREATE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

/**
 * @author Acrobot
 */
public class PlayerInteract implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Action action = event.getAction();
        Player player = event.getPlayer();

        if (Properties.USE_BUILT_IN_PROTECTION && uBlock.couldBeShopContainer(block)) {
            Sign sign = uBlock.getConnectedSign(block);
            if (sign != null) {

                if (!Security.canView(player, block, Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY)) {
                    if (Permission.has(player, Permission.SHOPINFO)) {
                        ChestShop.callEvent(new ShopInfoEvent(player, sign));
                        event.setCancelled(true);
                    } else if (!Properties.TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY) {
                        Messages.ACCESS_DENIED.send(player);
                        event.setCancelled(true);
                    }
                }

                return;
            }
        }

        if (!isSign(block))
            return;

        Sign sign = (Sign) getState(block, false);
        if (!ChestShopSign.isValid(sign)) {
            return;
        }

        if (Properties.ALLOW_AUTO_ITEM_FILL && ChatColor.stripColor(ChestShopSign.getItem(sign)).equals(AUTOFILL_CODE)) {
            if (ChestShopSign.hasPermission(player, OTHER_NAME_CREATE, sign)) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!MaterialUtil.isEmpty(item)) {
                    event.setCancelled(true);
                    String itemCode;
                    try {
                        itemCode = ItemUtil.getSignName(item);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(ChatColor.RED + "Error while generating shop sign item name. Please contact an admin or take a look at the console/log!");
                        com.Acrobot.ChestShop.ChestShop.getPlugin().getLogger().log(Level.SEVERE, "Error while generating shop sign item name", e);
                        return;
                    }
                    String[] lines = sign.getLines();
                    lines[ITEM_LINE] = itemCode;

                    SignChangeEvent changeEvent = new SignChangeEvent(block, player, lines);
                    com.Acrobot.ChestShop.ChestShop.callEvent(changeEvent);
                    if (!changeEvent.isCancelled()) {
                        for (byte i = 0; i < changeEvent.getLines().length; ++i) {
                            String line = changeEvent.getLine(i);
                            sign.setLine(i, line != null ? line : "");
                        }
                        sign.update();
                    }
                } else {
                    Messages.NO_ITEM_IN_HAND.sendWithPrefix(player);
                }
            } else {
                Messages.ACCESS_DENIED.sendWithPrefix(player);
            }
            return;
        }

        boolean notAllowedToTrade = ChestShopSign.isOwner(player, sign)
                || (Properties.IGNORE_ACCESS_PERMS && ChestShopSign.canAccess(player, sign) && !AccessToggle.isIgnoring(player));
        if (notAllowedToTrade && player.getInventory().getItemInMainHand().getType().name().contains("SIGN") && action == RIGHT_CLICK_BLOCK) {
            // Allow editing of sign (if supported)
            return;
        } else if ((player.getInventory().getItemInMainHand().getType().name().endsWith("DYE")
                || player.getInventory().getItemInMainHand().getType().name().endsWith("INK_SAC"))
                && action == RIGHT_CLICK_BLOCK) {
            if (notAllowedToTrade && Properties.SIGN_DYING) {
                return;
            } else {
                event.setCancelled(true);
            }
        }

        if (notAllowedToTrade && ChestShopSign.canAccess(player, sign) && !ChestShopSign.isAdminShop(sign)) {
            if (Properties.ALLOW_SIGN_CHEST_OPEN && !(Properties.IGNORE_CREATIVE_MODE && player.getGameMode() == GameMode.CREATIVE)) {
                if (player.isSneaking() || player.isInsideVehicle()
                        || (Properties.ALLOW_LEFT_CLICK_DESTROYING && action == LEFT_CLICK_BLOCK)) {
                    return;
                }
                event.setCancelled(true);
                showChestGUI(player, block, sign);
                return;
            }
            // don't allow owners or people with access to buy/sell at this shop
            Messages.TRADE_DENIED_ACCESS_PERMS.sendWithPrefix(player);
            if (action == RIGHT_CLICK_BLOCK) {
                // don't allow editing
                event.setCancelled(true);
            }
            return;
        }

        if (action == RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        } else if (action == LEFT_CLICK_BLOCK && !Properties.TURN_OFF_SIGN_PROTECTION && !ChestShopSign.canAccess(player, sign)) {
            event.setCancelled(true);
        }

        if (Properties.CHECK_ACCESS_FOR_SHOP_USE && !Security.canAccess(player, block, true)) {
            Messages.TRADE_DENIED.sendWithPrefix(player);
            return;
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
        String name = ChestShopSign.getOwner(sign);
        String prices = ChestShopSign.getPrice(sign);
        String material = ChestShopSign.getItem(sign);

        AccountQueryEvent accountQueryEvent = new AccountQueryEvent(name);
        Bukkit.getPluginManager().callEvent(accountQueryEvent);
        Account account = accountQueryEvent.getAccount();
        if (account == null) {
            Messages.PLAYER_NOT_FOUND.sendWithPrefix(player);
            return null;
        }

        boolean adminShop = ChestShopSign.isAdminShop(sign);

        // check if player exists in economy
        if (!adminShop) {
            AccountCheckEvent event = new AccountCheckEvent(account.getUuid(), player.getWorld());
            Bukkit.getPluginManager().callEvent(event);
            if(!event.hasAccount()) {
                Messages.NO_ECONOMY_ACCOUNT.sendWithPrefix(player);
                return null;
            }
        }

        Action buy = Properties.REVERSE_BUTTONS ? LEFT_CLICK_BLOCK : RIGHT_CLICK_BLOCK;
        BigDecimal price = (action == buy ? PriceUtil.getExactBuyPrice(prices) : PriceUtil.getExactSellPrice(prices));

        Container shopBlock = uBlock.findConnectedContainer(sign);
        Inventory ownerInventory = shopBlock != null ? shopBlock.getInventory() : null;

        ItemParseEvent parseEvent = new ItemParseEvent(material);
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack item = parseEvent.getItem();
        if (item == null) {
            Messages.INVALID_SHOP_DETECTED.sendWithPrefix(player);
            return null;
        }

        int amount = -1;
        try {
            amount = ChestShopSign.getQuantity(sign);
        } catch (NumberFormatException ignored) {} // There is no quantity number on the sign

        if (amount < 1 || amount > Properties.MAX_SHOP_AMOUNT) {
            Messages.INVALID_SHOP_PRICE.sendWithPrefix(player);
            return null;
        }

        BigDecimal pricePerItem = price.divide(BigDecimal.valueOf(amount), MathContext.DECIMAL128);
        if (Properties.SHIFT_SELLS_IN_STACKS && player.isSneaking() && !price.equals(PriceUtil.NO_PRICE) && isAllowedForShift(action == buy)) {
            int newAmount = adminShop ? InventoryUtil.getMaxStackSize(item) : getStackAmount(item, ownerInventory, player, action);
            if (newAmount > 0) {
                price = pricePerItem.multiply(BigDecimal.valueOf(newAmount)).setScale(Properties.PRICE_PRECISION, RoundingMode.HALF_UP);
                amount = newAmount;
            }
        } else if (Properties.SHIFT_SELLS_EVERYTHING && player.isSneaking() && !price.equals(PriceUtil.NO_PRICE) && isAllowedForShift(action == buy)) {
            if (action != buy) {
                int newAmount = InventoryUtil.getAmount(item, player.getInventory());
                if (newAmount > 0) {
                    price = pricePerItem.multiply(BigDecimal.valueOf(newAmount)).setScale(Properties.PRICE_PRECISION, RoundingMode.HALF_UP);
                    amount = newAmount;
                }
            } else if (!adminShop && ownerInventory != null) {
                int newAmount = InventoryUtil.getAmount(item, ownerInventory);
                if (newAmount > 0) {
                    price = pricePerItem.multiply(BigDecimal.valueOf(newAmount)).setScale(Properties.PRICE_PRECISION, RoundingMode.HALF_UP);
                    amount = newAmount;
                }
            }
        }

        item.setAmount(amount);

        ItemStack[] items = InventoryUtil.getItemsStacked(item);

        // Create virtual admin inventory if
        // - it's an admin shop
        // - there is no container for the shop sign
        // - the config doesn't force unlimited admin shop stock
        if (adminShop && (ownerInventory == null || Properties.FORCE_UNLIMITED_ADMIN_SHOP)) {
            ownerInventory = new AdminInventory(action == buy ? Arrays.stream(items).map(ItemStack::clone).toArray(ItemStack[]::new) : new ItemStack[0]);
        }

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

    /**
     * @deprecated Use {@link ChestShopSign#hasPermission(Player, Permission, Sign)} with {@link Permission#OTHER_NAME_ACCESS}
     */
    @Deprecated
    public static boolean canOpenOtherShops(Player player) {
        return Permission.has(player, Permission.OTHER_NAME_ACCESS + ".*");
    }

    private static void showChestGUI(Player player, Block signBlock, Sign sign) {
        Container container = uBlock.findConnectedContainer(sign);

        if (container == null) {
            Messages.NO_CHEST_DETECTED.sendWithPrefix(player);
            return;
        }

        if (!Security.canAccess(player, signBlock)) {
            return;
        }
        
        if (!Security.canAccess(player, container.getBlock())) {
            return;
        }

        BlockUtil.openBlockGUI(container, player);
    }
}
