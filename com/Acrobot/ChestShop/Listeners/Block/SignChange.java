package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.MaxPrice;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.PriceUtil.NO_PRICE;
import static com.Acrobot.ChestShop.Config.Language.*;
import static com.Acrobot.ChestShop.Config.Property.SHOP_CREATION_PRICE;
import static com.Acrobot.ChestShop.Config.Property.STICK_SIGNS_TO_CHESTS;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.*;

/**
 * @author Acrobot
 */
public class SignChange implements Listener {
    @EventHandler(ignoreCancelled = true)
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();
        String[] line = event.getLines();

        if (!BlockUtil.isSign(signBlock)) {
            ChestShop.getBukkitLogger().severe("Player " + event.getPlayer().getName() + " tried to create a fake sign. Hacking?");
            return;
        }

        ItemStack stock = MaterialUtil.getItem(line[ITEM_LINE]);

        if (!ChestShopSign.isValidPreparedSign(line)) {
            return;
        }

        if (stock == null) {
            sendMessageAndExit(INCORRECT_ITEM_ID, event);
            return;
        }

        Player player = event.getPlayer();
        boolean isAdmin = Permission.has(player, Permission.ADMIN);

        if (!playerCanUseName(player, line[NAME_LINE])) {
            event.setLine(NAME_LINE, uName.stripName(player.getName()));
        }

        String formattedPrice = formatPriceLine(line[PRICE_LINE]);

        if (formattedPrice == null) {
            sendMessageAndExit(YOU_CANNOT_CREATE_SHOP, event);
            return;
        }

        event.setLine(PRICE_LINE, formattedPrice);
        event.setLine(ITEM_LINE, formatItemLine(line[ITEM_LINE], stock));

        Chest connectedChest = uBlock.findConnectedChest(signBlock);

        if (!isAdminShop(line[NAME_LINE])) {
            if (connectedChest == null) {
                sendMessageAndExit(NO_CHEST_DETECTED, event);
                return;
            }

            if (!isAdmin && !Security.canPlaceSign(player, (Sign) signBlock.getState())) {
                sendMessageAndExit(CANNOT_CREATE_SHOP_HERE, event);
                return;
            }

            BuildPermissionEvent bEvent = new BuildPermissionEvent(player, connectedChest.getLocation(), signBlock.getLocation());
            ChestShop.callEvent(bEvent);

            if (!bEvent.isAllowed()) {
                sendMessageAndExit(CANNOT_CREATE_SHOP_HERE, event);
                return;
            }

            if (!isAdmin && !Security.canAccess(player, connectedChest.getBlock())) {
                sendMessageAndExit(CANNOT_ACCESS_THE_CHEST, event);
                return;
            }
        }

        double buyPrice = PriceUtil.getBuyPrice(formattedPrice);
        double sellPrice = PriceUtil.getSellPrice(formattedPrice);

        if (!isAdmin && (!canCreateShop(player, stock.getType(), buyPrice, sellPrice) || !MaxPrice.canCreate(buyPrice, sellPrice, stock.getType()))) {
            sendMessageAndExit(YOU_CANNOT_CREATE_SHOP, event);
            return;
        }

        float shopCreationPrice = Config.getFloat(SHOP_CREATION_PRICE);
        if (shopCreationPrice != 0 && !ChestShopSign.isAdminShop(line[NAME_LINE]) && !Permission.has(player, Permission.NOFEE)) {
            if (!Economy.hasEnough(player.getName(), shopCreationPrice)) {
                sendMessageAndExit(NOT_ENOUGH_MONEY, event);
                return;
            }

            Economy.subtract(player.getName(), shopCreationPrice);

            player.sendMessage(Config.getLocal(SHOP_CREATED) + " - " + Economy.formatBalance(shopCreationPrice));
        } else {
            player.sendMessage(Config.getLocal(SHOP_CREATED));
        }

        if (!isAdminShop(line[NAME_LINE]) && Config.getBoolean(STICK_SIGNS_TO_CHESTS)) {
            stickSign(signBlock, event);
        }

        ShopCreatedEvent sEvent = new ShopCreatedEvent(player, (Sign) signBlock.getState(), connectedChest, event.getLines());
        ChestShop.callEvent(sEvent);
    }

    private static void stickSign(Block block, SignChangeEvent event) {
        if (block.getType() != Material.SIGN_POST) {
            return;
        }

        BlockFace chestFace = null;

        for (BlockFace face : uBlock.CHEST_EXTENSION_FACES) {
            if (block.getRelative(face).getType() == Material.CHEST) {
                chestFace = face;
                break;
            }
        }

        if (chestFace == null) {
            return;
        }

        org.bukkit.material.Sign signMaterial = new org.bukkit.material.Sign(Material.WALL_SIGN);
        signMaterial.setFacingDirection(chestFace.getOppositeFace());

        block.setType(Material.WALL_SIGN);
        block.setData(signMaterial.getData());

        Sign sign = (Sign) block.getState();

        for (int i = 0; i < event.getLines().length; ++i) {
            sign.setLine(i, event.getLine(i));
        }

        sign.update(true);
    }

    private static boolean canCreateShop(Player player, Material mat, double buyPrice, double sellPrice) {
        if (Config.getBoolean(Property.BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE)) {
            if (buyPrice != NO_PRICE && sellPrice != NO_PRICE && sellPrice > buyPrice) {
                return false;
            }
        }
        return canCreateShop(player, mat, buyPrice != -1, sellPrice != -1) && MaxPrice.canCreate(buyPrice, sellPrice, mat);
    }

    private static boolean canCreateShop(Player player, Material material, boolean buy, boolean sell) {
        if (Permission.has(player, Permission.SHOP_CREATION_ID + Integer.toString(material.getId()))) {
            return true;
        }

        if (buy && !Permission.has(player, Permission.SHOP_CREATION_BUY)){
            return false;
        } else {
            return !(sell && !Permission.has(player, Permission.SHOP_CREATION_SELL));
        }
    }

    private static String formatPriceLine(String thirdLine) {
        String line = thirdLine;
        String[] split = line.toUpperCase().split(":");

        if (PriceUtil.textIsPrice(split[0])) {
            line = "B " + line;
        }
        if (split.length == 2 && PriceUtil.textIsPrice(split[1])) {
            line += " S";
        }

        if (line.length() > 15) {
            line = line.replace(" ", "");
        }

        line = line.replace('b', 'B').replace('s', 'S');

        return (line.length() > 15 ? null : line);
    }

    private static String formatItemLine(String line, ItemStack item) {
        String formatted, data = "";
        String[] split = line.split(":|-", 2);

        if (MaterialUtil.ENCHANTMENT.matcher(line).matches()) {
            data = '-' + MaterialUtil.ENCHANTMENT.matcher(line).group();
        }

        String longItemName = MaterialUtil.getName(item, true);

        if (longItemName.length() < (15 - data.length()) && MaterialUtil.equals(MaterialUtil.getItem(longItemName + data), item)) {
            return StringUtil.capitalizeFirstLetter(longItemName + data);
        }

        formatted = MaterialUtil.getName(item, false);
        data = (split.length == 2 ? split[1] : "");

        if (formatted.length() > (15 - data.length())) {
            formatted = formatted.substring(0, (15 - data.length()));
        }

        if (MaterialUtil.getItem(formatted).getType() != item.getType()) {
            formatted = String.valueOf(item.getTypeId());
        }

        if (split.length == 2) {
            int dataValuePos = line.indexOf(split[1], split[0].length());
            formatted += line.charAt(dataValuePos - 1) + split[1];
        }

        return StringUtil.capitalizeFirstLetter(formatted);
    }

    private static boolean playerCanUseName(Player player, String name) {
        return !name.isEmpty() && (uName.canUseName(player, name) || Permission.has(player, Permission.ADMIN));
    }

    private static void sendMessageAndExit(Language message, SignChangeEvent event) {
        event.getPlayer().sendMessage(Config.getLocal(message));

        dropSign(event);
    }

    private static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);
        event.getBlock().breakNaturally();
    }
}
