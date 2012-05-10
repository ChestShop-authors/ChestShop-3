package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Containers.Container;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Logging.Logging;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Utils.uInventory;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Shop {
    private final short durability;
    private final Container chest;

    public final ItemStack stock;
    public int stockAmount;
    public final String owner;
    public final Sign sign;

    public Shop(Container chest, Sign sign, ItemStack... itemStacks) {
        this.stock = itemStacks[0];
        this.durability = stock.getDurability();
        this.chest = chest;
        this.owner = sign.getLine(0);
        this.stockAmount = uSign.itemAmount(sign.getLine(1));
        this.sign = sign;
    }

    public void buyItemFrom(Player player) {
        double buyPrice = uSign.buyPrice(sign.getLine(2));

        if (chest == null) {
            sendMessage(player, Language.NO_CHEST_DETECTED);
            return;
        }
        if (Double.compare(buyPrice, 0.01D) < 0) {
            sendMessage(player, Language.NO_BUYING_HERE);
            return;
        }

        if (!Permission.has(player, Permission.BUY) && !Permission.has(player, Permission.BUY_ID + Integer.toString(stock.getTypeId()))) {
            sendMessage(player, Language.NO_PERMISSION);
            return;
        }
        String playerName = player.getName();

        if (!Economy.hasEnough(playerName, buyPrice)) {
            int items = calculateItemAmount(Economy.balance(playerName), buyPrice);
            if (!Config.getBoolean(Property.ALLOW_PARTIAL_TRANSACTIONS) || items < 1) {
                sendMessage(player, Language.NOT_ENOUGH_MONEY);
                return;
            } else {
                buyPrice = (buyPrice / stockAmount) * items;
                stockAmount = items;
            }
        }
        if (!stockFitsPlayer(player)) {
            sendMessage(player, Language.NOT_ENOUGH_SPACE_IN_INVENTORY);
            return;
        }

        String materialName = uSign.capitalizeFirstLetter(stock.getType().name());

        if (!hasEnoughStock()) {
            int items = stockAmount(stock, durability);
            if (!Config.getBoolean(Property.ALLOW_PARTIAL_TRANSACTIONS) || items < 1) {
                sendMessage(player, Language.NOT_ENOUGH_STOCK);

                if (!Config.getBoolean(Property.SHOW_MESSAGE_OUT_OF_STOCK)) {
                    return;
                }

                sendMessageToOwner(Config.getLocal(Language.NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", materialName));
                return;
            } else {
                buyPrice = (buyPrice / stockAmount) * items;
                stockAmount = items;
            }
        }

        Economy.add(getOwnerAccount(), buyPrice);
        Economy.subtract(playerName, buyPrice);

        chest.removeItem(stock, durability, stockAmount);


        String formatedPrice = Economy.formatBalance(buyPrice);
        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            String message = formatMessage(Language.YOU_BOUGHT_FROM_SHOP, materialName, formatedPrice);
            message = message.replace("%owner", owner);

            player.sendMessage(message);
        }

        uInventory.add(player.getInventory(), stock, stockAmount);
        Logging.logTransaction(true, this, buyPrice, player);
        player.updateInventory();

        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_OWNER)) {
            String message = formatMessage(Language.SOMEBODY_BOUGHT_FROM_YOUR_SHOP, materialName, formatedPrice);
            message = message.replace("%buyer", player.getName());

            sendMessageToOwner(message);
        }

        if (shopShouldBeRemoved()) {
            removeShop();
        }
    }

    public void sellItemTo(Player player) {
        double sellPrice = uSign.sellPrice(sign.getLine(2));

        if (chest == null) {
            sendMessage(player, Language.NO_CHEST_DETECTED);
            return;
        }
        if (Double.compare(sellPrice, 0.01D) < 0) {
            sendMessage(player, Language.NO_SELLING_HERE);
            return;
        }
        if (!Permission.has(player, Permission.SELL) && !Permission.has(player, Permission.SELL_ID + Integer.toString(stock.getTypeId()))) {
            sendMessage(player, Language.NO_PERMISSION);
            return;
        }

        String account = getOwnerAccount();

        if (!Economy.hasEnough(account, sellPrice)) {
            int items = calculateItemAmount(Economy.balance(account), sellPrice);
            if (!Config.getBoolean(Property.ALLOW_PARTIAL_TRANSACTIONS) || items < 1) {
                sendMessage(player, Language.NOT_ENOUGH_MONEY_SHOP);
                return;
            } else {
                sellPrice = (sellPrice / stockAmount) * items;
                stockAmount = items;
            }
        }
        if (uInventory.amount(player.getInventory(), stock, durability) < stockAmount) {
            int items = uInventory.amount(player.getInventory(), stock, durability);
            if (!Config.getBoolean(Property.ALLOW_PARTIAL_TRANSACTIONS) || items < 1) {
                sendMessage(player, Language.NOT_ENOUGH_ITEMS_TO_SELL);
                return;
            } else {
                sellPrice = (sellPrice / stockAmount) * items;
                stockAmount = items;
            }
        }

        if (!stockFitsChest(chest)) {
            sendMessage(player, Language.NOT_ENOUGH_SPACE_IN_CHEST);
            return;
        }

        Economy.subtract(account, sellPrice);
        Economy.add(player.getName(), sellPrice);

        chest.addItem(stock, stockAmount);

        String materialName = uSign.capitalizeFirstLetter(stock.getType().name());
        String formatedBalance = Economy.formatBalance(sellPrice);

        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            String message = formatMessage(Language.YOU_SOLD_TO_SHOP, materialName, formatedBalance);
            message = message.replace("%buyer", owner);

            player.sendMessage(message);
        }

        uInventory.remove(player.getInventory(), stock, stockAmount, durability);

        Logging.logTransaction(false, this, sellPrice, player);

        player.updateInventory();

        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_OWNER)) {
            String message = formatMessage(Language.SOMEBODY_SOLD_TO_YOUR_SHOP, materialName, formatedBalance);
            message = message.replace("%seller", player.getName());

            sendMessageToOwner(message);
        }
    }

    private boolean shopShouldBeRemoved() {
        return Config.getBoolean(Property.REMOVE_EMPTY_SHOPS) && shopIsEmpty();
    }

    private boolean shopIsEmpty() {
        return chest.isEmpty();
    }

    private void removeShop() {
        sign.getBlock().setType(Material.AIR);

        chest.addItem(new ItemStack(Material.SIGN, 1), 1);
    }

    private String formatMessage(Language message, String materialName, String price) {
        return Config.getLocal(message)
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%price", price);
    }

    private String getOwnerAccount() {
        return isAdminShop() ? Config.getString(Property.SERVER_ECONOMY_ACCOUNT) : owner;
    }

    private boolean isAdminShop() {
        return uSign.isAdminShop(owner);
    }

    private boolean hasEnoughStock() {
        return chest.hasEnough(stock, stockAmount, durability);
    }

    private int stockAmount(ItemStack item, short durability) {
        return chest.amount(item, durability);
    }

    private boolean stockFitsPlayer(Player player) {
        return uInventory.fits(player.getInventory(), stock, stockAmount, durability) <= 0;
    }

    private boolean stockFitsChest(Container chest) {
        return chest.fits(stock, stockAmount, durability);
    }

    private int calculateItemAmount(double money, double basePrice) {
        return (int) Math.floor(money / (basePrice / stockAmount));
    }

    private static void sendMessage(Player player, Language message) {
        player.sendMessage(Config.getLocal(message));
    }

    private void sendMessageToOwner(String msg) {
        if (!isAdminShop()) {
            Player player = ChestShop.getBukkitServer().getPlayer(owner);
            if (player != null) {
                player.sendMessage(msg);
            }
        }
    }
}
