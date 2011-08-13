package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Chests.ChestObject;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Logging.Logging;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Utils.uInventory;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Shop {
    private final short durability;
    private final ChestObject chest;

    public final ItemStack stock;
    public final int stockAmount;
    public final float buyPrice;
    public final float sellPrice;
    public final String owner;

    public Shop(ChestObject chest, boolean buy, Sign sign, ItemStack... itemStacks) {
        this.stock = itemStacks[0];
        this.durability = stock.getDurability();
        this.chest = chest;
        this.buyPrice = (buy ? uSign.buyPrice(sign.getLine(2)) : -1);
        this.sellPrice = (!buy ? uSign.sellPrice(sign.getLine(2)) : -1);
        this.owner = sign.getLine(0);
        this.stockAmount = uSign.itemAmount(sign.getLine(1));
    }

    public void buy(Player player) {
        if (chest == null && !isAdminShop()) {
            player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
            return;
        }
        if (buyPrice == -1) {
            player.sendMessage(Config.getLocal(Language.NO_BUYING_HERE));
            return;
        }
        if (!Permission.has(player, Permission.BUY) && !Permission.has(player, Permission.BUY_ID + Integer.toString(stock.getTypeId()))) {
            player.sendMessage(Config.getLocal(Language.NO_PERMISSION));
            return;
        }
        String playerName = player.getName();
        if (!Economy.hasEnough(playerName, buyPrice)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY));
            return;
        }
        if (!stockFitsPlayer(player)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_SPACE_IN_INVENTORY));
            return;
        }

        String materialName = stock.getType().name();

        if (!isAdminShop() && !hasEnoughStock()) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_STOCK));
            if (!Config.getBoolean(Property.SHOW_MESSAGE_OUT_OF_STOCK)) return;
            sendMessageToOwner(Config.getLocal(Language.NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", materialName));
            return;
        }

        String account = getOwnerAccount();
        if (!account.isEmpty() && Economy.hasAccount(account)) Economy.add(account, buyPrice);

        Economy.substract(playerName, buyPrice);

        if (!isAdminShop()) chest.removeItem(stock, durability, stockAmount);

        String formatedPrice = Economy.formatBalance(buyPrice);
        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            player.sendMessage(Config.getLocal(Language.YOU_BOUGHT_FROM_SHOP)
                    .replace("%amount", String.valueOf(stockAmount))
                    .replace("%item", materialName)
                    .replace("%owner", owner)
                    .replace("%price", formatedPrice));
        }

        uInventory.add(player.getInventory(), stock, stockAmount);
        Logging.logTransaction(true, this, player);
        player.updateInventory();

        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_OWNER)) {
            sendMessageToOwner(Config.getLocal(Language.SOMEBODY_BOUGHT_FROM_YOUR_SHOP)
                    .replace("%amount", String.valueOf(stockAmount))
                    .replace("%item", materialName)
                    .replace("%buyer", playerName)
                    .replace("%price", formatedPrice));
        }
    }

    public void sell(Player player) {
        if (chest == null && !isAdminShop()) {
            player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
            return;
        }
        if (sellPrice == -1) {
            player.sendMessage(Config.getLocal(Language.NO_SELLING_HERE));
            return;
        }
        if (!Permission.has(player, Permission.SELL) && !Permission.has(player, Permission.SELL_ID + Integer.toString(stock.getTypeId()))) {
            player.sendMessage(Config.getLocal(Language.NO_PERMISSION));
            return;
        }

        String account = getOwnerAccount();
        boolean accountExists = !account.isEmpty() && Economy.hasAccount(account);

        if (accountExists && !Economy.hasEnough(account, sellPrice)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY_SHOP));
            return;
        }
        if (!isAdminShop() && !stockFitsChest(chest)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_SPACE_IN_CHEST));
            return;
        }

        if (uInventory.amount(player.getInventory(), stock, durability) < stockAmount) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_ITEMS_TO_SELL));
            return;
        }


        if (accountExists) Economy.substract(account, sellPrice);
        if (!isAdminShop()) chest.addItem(stock, stockAmount);

        Economy.add(player.getName(), sellPrice);

        String materialName = stock.getType().name();
        String formatedBalance = Economy.formatBalance(sellPrice);

        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_CLIENT)) {
            player.sendMessage(Config.getLocal(Language.YOU_SOLD_TO_SHOP)
                    .replace("%amount", String.valueOf(stockAmount))
                    .replace("%item", materialName)
                    .replace("%buyer", owner)
                    .replace("%price", formatedBalance));
        }

        uInventory.remove(player.getInventory(), stock, stockAmount, durability);
        Logging.logTransaction(false, this, player);
        player.updateInventory();

        if (Config.getBoolean(Property.SHOW_TRANSACTION_INFORMATION_OWNER)) {
            sendMessageToOwner(Config.getLocal(Language.SOMEBODY_SOLD_TO_YOUR_SHOP)
                    .replace("%amount", String.valueOf(stockAmount))
                    .replace("%item", materialName)
                    .replace("%seller", player.getName())
                    .replace("%price", formatedBalance));
        }
    }

    private String getOwnerAccount() {
        return uSign.isAdminShop(owner) ? Config.getString(Property.SERVER_ECONOMY_ACCOUNT) : owner;
    }

    private boolean isAdminShop() {
        return uSign.isAdminShop(owner);
    }

    private boolean hasEnoughStock() {
        return chest.hasEnough(stock, stockAmount, durability);
    }

    private boolean stockFitsPlayer(Player player) {
        return uInventory.fits(player.getInventory(), stock, stockAmount, durability) <= 0;
    }

    private boolean stockFitsChest(ChestObject chest) {
        return chest.fits(stock, stockAmount, durability);
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
