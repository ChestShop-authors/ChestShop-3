package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.MaxPrice;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.BuildPermissionEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uName;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.Acrobot.ChestShop.Utils.uSign;
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

/**
 * @author Acrobot
 */
public class SignChange implements Listener {
    @EventHandler
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();
        String[] line = event.getLines();

        boolean isAlmostReady = uSign.isValidPreparedSign(line);

        Player player = event.getPlayer();
        ItemStack stock = Items.getItemStack(line[3]);
        Material mat = stock == null ? null : stock.getType();

        boolean playerIsAdmin = Permission.has(player, Permission.ADMIN);

        if (isAlmostReady) {
            if (mat == null) {
                player.sendMessage(Config.getLocal(Language.INCORRECT_ITEM_ID));
                dropSign(event);
                return;
            }
        } else {
            if (restrictedSign.isRestricted(line)) {
                if (!restrictedSign.hasPermission(player, line)) {
                    player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                    dropSign(event);
                    return;
                }
                Block secondSign = signBlock.getRelative(BlockFace.DOWN);
                if (!playerIsAdmin && (!uSign.isSign(secondSign) || !uSign.isValid((Sign) secondSign.getState())
                        || !uSign.canAccess(player, (Sign) secondSign))) dropSign(event);
            }
            return;
        }

        if (!playerCanUseName(player, line[0])) {
            event.setLine(0, uName.stripName(player.getName()));
        }

        String thirdLine = formatThirdLine(line[2]);
        if (thirdLine == null) {
            dropSign(event);
            player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
            return;
        }
        event.setLine(2, thirdLine);
        event.setLine(3, formatFourthLine(line[3], stock));

        Chest chest = uBlock.findConnectedChest(signBlock);

        boolean isAdminShop = uSign.isAdminShop(event.getLine(0));
        if (!isAdminShop) {
            if (chest == null) {
                player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
                dropSign(event);
                return;
            } else if (!playerIsAdmin) {
                if (!Security.canPlaceSign(player, (Sign) signBlock.getState())) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_CREATE_SHOP_HERE));
                    dropSign(event);
                    return;
                }

                Block chestBlock = chest.getBlock();
                BuildPermissionEvent bEvent = new BuildPermissionEvent(player, chest.getLocation(), signBlock.getLocation());

                ChestShop.callEvent(bEvent);

                if (!bEvent.isAllowed()) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_CREATE_SHOP_HERE));
                    dropSign(event);
                    return;
                }

                if (!Security.canAccess(player, chestBlock)) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_ACCESS_THE_CHEST));
                    dropSign(event);
                    return;
                }
            }
        }

        double buyPrice = uSign.buyPrice(thirdLine);
        double sellPrice = uSign.sellPrice(thirdLine);

        if (!playerIsAdmin && (!canCreateShop(player, mat, buyPrice != -1, sellPrice != -1) || !MaxPrice.canCreate(buyPrice, sellPrice, mat))) {
            player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
            dropSign(event);
            return;
        }

        float shopCreationPrice = Config.getFloat(Property.SHOP_CREATION_PRICE);
        boolean paid = shopCreationPrice != 0 && !isAdminShop && !Permission.has(player, Permission.NOFEE);
        if (paid) {
            if (!Economy.hasEnough(player.getName(), shopCreationPrice)) {
                player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY));
                dropSign(event);
                return;
            }

            Economy.subtract(player.getName(), shopCreationPrice);
        }

        if (Config.getBoolean(Property.PROTECT_SIGN_WITH_LWC)) {
            if (!Security.protect(player.getName(), signBlock)) player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_PROTECTIONS));
        }
        if (Config.getBoolean(Property.PROTECT_CHEST_WITH_LWC) && chest != null && Security.protect(player.getName(), chest.getBlock())) {
            player.sendMessage(Config.getLocal(Language.PROTECTED_SHOP));
        }

        uName.saveName(player.getName());
        player.sendMessage(Config.getLocal(Language.SHOP_CREATED) + (paid ? " - " + Economy.formatBalance(shopCreationPrice) : ""));

        ShopCreatedEvent sEvent = new ShopCreatedEvent(player, (Sign) signBlock.getState(), chest, event.getLines());
        ChestShop.callEvent(sEvent);
    }

    private static boolean canCreateShop(Player player, Material mat, double buyPrice, double sellPrice) {
        if (Config.getBoolean(Property.BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE)) {
            if (buyPrice != -1 && sellPrice != -1 && sellPrice > buyPrice) {
                return false;
            }
        }
        return canCreateShop(player, mat, buyPrice != -1, sellPrice != -1) && MaxPrice.canCreate(buyPrice, sellPrice, mat);
    }

    private static boolean canCreateShop(Player player, Material material, boolean buy, boolean sell) {
        if (Permission.has(player, Permission.SHOP_CREATION_ID + Integer.toString(material.getId()))) {
            return true;
        }

        if (buy && !Permission.has(player, Permission.SHOP_CREATION_BUY)) return false;
        if (sell && !Permission.has(player, Permission.SHOP_CREATION_SELL)) return false;

        return true;
    }

    private static String formatThirdLine(String thirdLine) {
        String line = thirdLine.toUpperCase();
        String[] split = line.split(":");

        if (uNumber.isFloat(split[0])) {
            line = "B " + line;
        }
        if (split.length == 2 && uNumber.isFloat(split[1])) {
            line = line + " S";
        }

        if (line.length() > 15) {
            line.replace(" ", "");
        }

        return (line.length() > 15 ? null : line);
    }

    private static String formatFourthLine(String line, ItemStack itemStack) {
        StringBuilder formatted = new StringBuilder(15);

        String[] split = line.split(":|-", 2);
        String itemName = Items.getName(itemStack, false);

        short dataLength = (short) (line.length() - split[0].length());

        if (itemName.length() > (15 - dataLength)) {
            itemName = itemName.substring(0, 15 - dataLength);
        }

        if (Items.getItemStack(itemName).getType() != itemStack.getType()) {
            itemName = String.valueOf(itemStack.getTypeId());
        }

        formatted.append(itemName);
        if (split.length == 2) {
            formatted.append(line.charAt(line.indexOf(split[1]) - 1)).append(split[1]);
        }

        return uSign.capitalizeFirstLetter(formatted.toString(), ' ');
    }

    private static boolean playerCanUseName(Player player, String name) {
        return !name.isEmpty() && (uName.canUseName(player, name) || Permission.has(player, Permission.ADMIN));
    }

    private static void sendMessageAndExit(Player player, Language message, SignChangeEvent event) {
        player.sendMessage(Config.getLocal(message));

        dropSign(event);
    }

    private static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);
        event.getBlock().breakNaturally();
    }
}
