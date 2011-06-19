package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Chests.ChestObject;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Logging.Logging;
import com.Acrobot.ChestShop.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Shop {
    public ItemStack stock;
    public short durability;
    public int stockAmount;
    public ChestObject chest;
    public float buyPrice;
    public float sellPrice;
    public String owner;

    public Shop(ChestObject chest, Sign sign, ItemStack... itemStacks) {
        this.stock = itemStacks[0];
        this.durability = stock.getDurability();
        this.chest = chest;
        this.buyPrice = SignUtil.buyPrice(sign.getLine(2));
        this.sellPrice = SignUtil.sellPrice(sign.getLine(2));
        this.owner = sign.getLine(0);
        this.stockAmount = SignUtil.itemAmount(sign.getLine(1));
    }

    public boolean buy(Player player) {
        if (chest == null && !isAdminShop()) {
            player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
            return false;
        }
        if (buyPrice == -1) {
            player.sendMessage(Config.getLocal(Language.NO_BUYING_HERE));
            return false;
        }
        String playerName = player.getName();
        if (!Economy.hasEnough(playerName, buyPrice)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY));
            return false;
        }
        if (!stockFitsPlayer(player)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_SPACE_IN_INVENTORY));
            return false;
        }

        String materialName = stock.getType().name();

        if (!isAdminShop() && !hasEnoughStock()) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_STOCK));
            sendMessageToOwner(Config.getLocal(Language.NOT_ENOUGH_STOCK_IN_YOUR_SHOP).replace("%material", materialName));
            return false;
        }

        String account = getOwnerAccount();
        if (!account.isEmpty() && Economy.hasAccount(account)) {
            Economy.add(account, buyPrice);
        }
        Economy.substract(playerName, buyPrice);

        if (!isAdminShop()) {
            chest.removeItem(stock, durability, stockAmount);
        }
        String formatedPrice = Economy.formatBalance(buyPrice);
        player.sendMessage(Config.getLocal(Language.YOU_BOUGHT_FROM_SHOP)
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%owner", owner)
                .replace("%price", formatedPrice));

        InventoryUtil.add(player.getInventory(), stock, stockAmount);
        Logging.logTransaction(true, this, player);
        player.updateInventory();

        sendMessageToOwner(Config.getLocal(Language.SOMEBODY_BOUGHT_FROM_YOUR_SHOP)
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%buyer", playerName)
                .replace("%price", formatedPrice));
        return true;
    }

    public boolean sell(Player player) {
        if (chest == null && !isAdminShop()) {
            player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
            return false;
        }
        if (sellPrice == -1) {
            player.sendMessage(Config.getLocal(Language.NO_SELLING_HERE));
            return false;
        }
        String account = getOwnerAccount();
        boolean accountExists = !account.isEmpty() && Economy.hasAccount(account);

        if (accountExists) {
            if (!Economy.hasEnough(account, sellPrice)) {
                player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY_SHOP));
                return false;
            }
        }
        if (!isAdminShop() && !stockFitsChest(chest)) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_SPACE_IN_CHEST));
            return false;
        }

        if (InventoryUtil.amount(player.getInventory(), stock, durability) < stockAmount) {
            player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_ITEMS_TO_SELL));
            return false;
        }


        if (accountExists) {
            Economy.substract(account, sellPrice);
        }

        if (!isAdminShop()) {
            chest.addItem(stock, stockAmount);
        }

        Economy.add(player.getName(), sellPrice);

        String materialName = stock.getType().name();
        String formatedBalance = Economy.formatBalance(sellPrice);

        player.sendMessage(Config.getLocal(Language.YOU_SOLD_TO_SHOP)
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%buyer", owner)
                .replace("%price", formatedBalance));

        InventoryUtil.remove(player.getInventory(), stock, stockAmount, durability);
        Logging.logTransaction(false, this, player);
        player.updateInventory();

        sendMessageToOwner(Config.getLocal(Language.SOMEBODY_SOLD_TO_YOUR_SHOP)
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%seller", player.getName())
                .replace("%price", formatedBalance));


        return true;
    }

    private String getOwnerAccount() {
        if (SignUtil.isAdminShop(owner)) {
            return Config.getString(Property.SERVER_ECONOMY_ACCOUNT);
        } else {
            return owner;
        }
    }

    private boolean isAdminShop() {
        return SignUtil.isAdminShop(owner);
    }

    private boolean hasEnoughStock() {
        return chest.hasEnough(stock, stockAmount, durability);
    }

    private boolean stockFitsPlayer(Player player) {
        return InventoryUtil.fits(player.getInventory(), stock, stockAmount, durability) <= 0;
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
