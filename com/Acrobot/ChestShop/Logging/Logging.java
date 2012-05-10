package com.Acrobot.ChestShop.Logging;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Shop.Shop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Acrobot
 */
public class Logging {
    private static final Logger logger = ChestShop.getBukkitLogger();

    public static void log(String string) {
        logger.log(Level.INFO, string);
    }

    public static void logTransaction(boolean playerIsBuyingFromShop, Shop shop, double price, Player player) {
        StringBuilder builder = new StringBuilder(player.getName());

        if (playerIsBuyingFromShop) {
            builder.append(" bought ");
        } else {
            builder.append(" sold ");
        }

        builder.append(shop.stockAmount).append(' ');
        builder.append(Items.getSignName(shop.stock)).append(" for ");
        builder.append(price);

        if (playerIsBuyingFromShop) {
            builder.append(" from ");
        } else {
            builder.append(" to ");
        }

        builder.append(shop.owner).append(" at ");
        builder.append(locationToString(shop.sign.getLocation()));

        log(builder.toString());

        if (weShouldLogToDB()) {
            logToDatabase(playerIsBuyingFromShop, shop, price, player);
        }
    }

    private static boolean weShouldLogToDB() {
        return Config.getBoolean(Property.LOG_TO_DATABASE) || Config.getBoolean(Property.GENERATE_STATISTICS_PAGE);
    }

    private static String locationToString(Location loc) {
        return '[' + loc.getWorld().getName() + "] " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    private static void logToDatabase(boolean isBuying, Shop shop, double price, Player player) {
        Transaction transaction = new Transaction();

        transaction.setAmount(shop.stockAmount);
        transaction.setBuy(isBuying);

        ItemStack stock = shop.stock;

        transaction.setItemDurability(stock.getDurability());
        transaction.setItemID(stock.getTypeId());
        transaction.setPrice((float) price);
        transaction.setSec(System.currentTimeMillis() / 1000);
        transaction.setShopOwner(shop.owner);
        transaction.setShopUser(player.getName());

        Queue.addToQueue(transaction);
    }
}
