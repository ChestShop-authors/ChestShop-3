package com.Acrobot.ChestShop.Shop;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Containers.AdminChest;
import com.Acrobot.ChestShop.Containers.Container;
import com.Acrobot.ChestShop.Containers.ShopChest;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.PriceUtil.*;
import static com.Acrobot.ChestShop.Config.Language.*;
import static com.Acrobot.ChestShop.Config.Property.ALLOW_PARTIAL_TRANSACTIONS;
import static com.Acrobot.ChestShop.Config.Property.SHOW_MESSAGE_OUT_OF_STOCK;
import static com.Acrobot.ChestShop.Events.TransactionEvent.Type.BUY;
import static com.Acrobot.ChestShop.Events.TransactionEvent.Type.SELL;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;

/**
 * @author Acrobot
 */
public class Shop {
    private final Container container;
    private final String owner;

    private ItemStack stock;

    private final Sign sign;

    public Shop(Container container, ItemStack stock, Sign sign) {
        this.container = container;
        this.sign = sign;

        this.owner = sign.getLine(NAME_LINE);
        this.stock = stock;
    }

    public void buyFromPlayer(Player player) {
        Language message = sell(player);

        sendMessage(player, message);
    }

    public void sellToPlayer(Player player) {
        Language message = buy(player);

        sendMessage(player, message);
    }

    private Language buy(Player player) {
        double price = getBuyPrice(sign.getLine(PRICE_LINE));

        if (price == NO_PRICE) {
            return NO_BUYING_HERE;
        }

        if (container == null) {
            return NO_CHEST_DETECTED;
        }

        if (!hasPermission(player, stock.getType(), true)) {
            return NO_PERMISSION;
        }

        String playerName = player.getName();
        String itemName = StringUtil.capitalizeFirstLetter(stock.getType().name());
        double balance = Economy.balance(playerName);

        if (!Economy.hasEnough(playerName, price)) {
            int possiblePartialItemCount = calculateItemAmount(balance, price);

            if (!partialTransactionAllowed(possiblePartialItemCount)) {
                return NOT_ENOUGH_MONEY;
            } else {
                price = (price / stock.getAmount()) * possiblePartialItemCount;
                stock.setAmount(possiblePartialItemCount);
            }
        }

        if (!stockFitsPlayer(player)) {
            return NOT_ENOUGH_SPACE_IN_INVENTORY;
        }

        if (!shopHasEnoughItems()) {
            int possiblePartialItemCount = getStockAmount(stock);

            if (!partialTransactionAllowed(possiblePartialItemCount)) {
                if (Config.getBoolean(SHOW_MESSAGE_OUT_OF_STOCK)) {
                    sendMessageToOwner(Config.getLocal(NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", itemName));
                }

                return NOT_ENOUGH_STOCK;
            } else {
                price = (price / stock.getAmount()) * possiblePartialItemCount;
                stock.setAmount(possiblePartialItemCount);
            }
        }

        Economy.add(getOwnerAccount(), price);
        Economy.subtract(playerName, price);

        container.removeItem(stock.clone()); //Bad bukkit! You shouldn't change ItemStacks during the process!
        InventoryUtil.add(stock.clone(), player.getInventory());

        player.updateInventory();

        TransactionEvent event = new TransactionEvent(BUY, container, sign, player, this.owner, stock, price);
        ChestShop.callEvent(event);

        return null;
    }

    private Language sell(Player player) {
        double price = getSellPrice(sign.getLine(PRICE_LINE));

        if (container == null) {
            return NO_CHEST_DETECTED;
        }

        if (price == PriceUtil.NO_PRICE) {
            return NO_SELLING_HERE;
        }

        if (!hasPermission(player, stock.getType(), false)) {
            return NO_PERMISSION;
        }

        String ownerAccount = getOwnerAccount();

        if (!Economy.hasEnough(ownerAccount, price)) {
            int possiblePartialItemCount = calculateItemAmount(Economy.balance(ownerAccount), price);

            if (!partialTransactionAllowed(possiblePartialItemCount)) {
                return NOT_ENOUGH_MONEY_SHOP;
            } else {
                price = (price / stock.getAmount()) * possiblePartialItemCount;
                stock.setAmount(possiblePartialItemCount);
            }
        }

        if (!playerHasEnoughItems(player)) {
            int possiblePartialItemCount = InventoryUtil.getAmount(stock, player.getInventory());

            if (!partialTransactionAllowed(possiblePartialItemCount)) {
                return NOT_ENOUGH_ITEMS_TO_SELL;
            } else {
                price = (price / stock.getAmount()) * possiblePartialItemCount;
                stock.setAmount(possiblePartialItemCount);
            }
        }

        if (!stockFitsChest()) {
            return NOT_ENOUGH_SPACE_IN_CHEST;
        }

        Economy.subtract(ownerAccount, price);
        Economy.add(player.getName(), price);

        container.addItem(stock.clone());

        InventoryUtil.remove(stock.clone(), player.getInventory());
        player.updateInventory();

        TransactionEvent event = new TransactionEvent(SELL, container, sign, player, this.owner, stock, price);
        ChestShop.callEvent(event);

        return null;
    }

    private String getOwnerAccount() {
        return isAdminShop() ? Config.getString(Property.SERVER_ECONOMY_ACCOUNT) : owner;
    }

    private boolean isAdminShop() {
        return ChestShopSign.isAdminShop(owner);
    }

    private boolean stockFitsPlayer(Player player) {
        return InventoryUtil.fits(stock, player.getInventory());
    }

    private boolean stockFitsChest() {
        return container.fits(stock);
    }

    private int getStockAmount(ItemStack item) {
        return container.amount(item);
    }

    private boolean shopHasEnoughItems() {
        return container.hasEnough(stock);
    }

    private boolean playerHasEnoughItems(Player player) {
        return InventoryUtil.getAmount(stock, player.getInventory()) >= stock.getAmount();
    }

    private int calculateItemAmount(double money, double basePrice) {
        return (int) Math.floor(money / (basePrice / stock.getAmount()));
    }

    private static void sendMessage(Player player, Language message) {
        if (message != null) {
            player.sendMessage(Config.getLocal(message));
        }
    }

    private void sendMessageToOwner(String msg) {
        Player player = ChestShop.getBukkitServer().getPlayer(owner);

        if (player != null) {
            player.sendMessage(msg);
        }
    }

    public static Shop getShopFromSign(Sign sign) {
        Chest chestMc = uBlock.findConnectedChest(sign);
        ItemStack item = MaterialUtil.getItem(sign.getLine(ITEM_LINE));

        if (item == null) {
            return null;
        }

        int itemAmount = Integer.parseInt(sign.getLine(QUANTITY_LINE));
        item.setAmount(itemAmount);

        if (ChestShopSign.isAdminShop(sign)) {
            return new Shop(new AdminChest(), item, sign);
        } else {
            return new Shop(chestMc != null ? new ShopChest(chestMc) : null, item, sign);
        }
    }

    private static boolean partialTransactionAllowed(int itemCount) {
        return Config.getBoolean(ALLOW_PARTIAL_TRANSACTIONS) && itemCount > 0;
    }

    private static boolean hasPermission(Player player, Material material, boolean buying) {
        if (buying) {
            return Permission.has(player, Permission.BUY) || Permission.has(player, Permission.BUY_ID + Integer.toString(material.getId()));
        } else {
            return Permission.has(player, Permission.SELL) || Permission.has(player, Permission.SELL_ID + Integer.toString(material.getId()));
        }
    }
}
