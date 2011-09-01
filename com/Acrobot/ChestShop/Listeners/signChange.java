package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Plugins.Default;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uLongName;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class signChange extends BlockListener {

    public void onSignChange(SignChangeEvent event) {
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
            if (!canCreateShop(player, mat.getId())) {
                player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
                dropSign(event);
                return;
            }
        } else {
            if (restrictedSign.isRestricted(event.getLines())) {
                if (!playerIsAdmin) {
                    player.sendMessage(Config.getLocal(Language.ACCESS_DENIED));
                    dropSign(event);
                    return;
                }
                Block secondSign = signBlock.getRelative(BlockFace.DOWN);
                if (!uSign.isSign(secondSign) || !uSign.isValid((Sign) secondSign.getState())) dropSign(event);
            }
            return;
        }

        if (formatFirstLine(line[0], player)) event.setLine(0, uLongName.stripName(player.getName()));

        String thirdLine = formatThirdLine(line[2]);
        if (thirdLine == null) {
            dropSign(event);
            player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
            return;
        }
        event.setLine(2, thirdLine);
        event.setLine(3, formatFourthLine(line[3], mat));

        Chest chest = uBlock.findChest(signBlock);

        boolean isAdminShop = uSign.isAdminShop(event.getLine(0));
        if (!isAdminShop) {
            if (chest == null) {
                player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
                dropSign(event);
                return;
            } else if (!playerIsAdmin) {
                if (!Security.canPlaceSign(player, signBlock)) {
                    player.sendMessage(Config.getLocal(Language.ANOTHER_SHOP_DETECTED));
                    dropSign(event);
                    return;
                }

                Block chestBlock = chest.getBlock();
                boolean canAccess = !Security.isProtected(chestBlock) || Security.canAccess(player, chestBlock);

                if (!(Security.protection instanceof Default) && canAccess) {
                    Default protection = new Default();
                    if (protection.isProtected(chestBlock) && !protection.canAccess(player, chestBlock))
                        canAccess = false;
                }

                if (!canAccess) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_ACCESS_THE_CHEST));
                    dropSign(event);
                    return;
                }
            }
        }

        float shopCreationPrice = Config.getFloat(Property.SHOP_CREATION_PRICE);
        boolean paid = shopCreationPrice != 0 && !isAdminShop;
        if (paid) {
            if (!Economy.hasEnough(player.getName(), shopCreationPrice)) {
                player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_MONEY));
                dropSign(event);
                return;
            }

            Economy.substract(player.getName(), shopCreationPrice);
        }

        if (Config.getBoolean(Property.PROTECT_SIGN_WITH_LWC)) {
            Security.protect(player.getName(), signBlock);
        }
        if (Config.getBoolean(Property.PROTECT_CHEST_WITH_LWC) && chest != null && Security.protect(player.getName(), chest.getBlock())) {
            player.sendMessage(Config.getLocal(Language.PROTECTED_SHOP));
        }

        uLongName.saveName(player.getName());
        player.sendMessage(Config.getLocal(Language.SHOP_CREATED) + (paid ? " - " + Economy.formatBalance(shopCreationPrice) : ""));
    }

    private static boolean canCreateShop(Player player, boolean isAdmin, int ID) {
        return isAdmin ||
                Permission.has(player, Permission.SHOP_CREATION) ||
                Permission.has(player, Permission.SHOP_CREATION.toString() + '.' + ID);
    }

    private static boolean canCreateShop(Player player, int ID) {
        return canCreateShop(player, Permission.has(player, Permission.ADMIN), ID);
    }

    private static String formatThirdLine(String thirdLine) {
        String[] split = thirdLine.split(":");
        if (uNumber.isFloat(split[0])) thirdLine = "B " + thirdLine;
        if (split.length == 2 && uNumber.isFloat(split[1])) thirdLine = thirdLine + " S";
        if (thirdLine.length() > 15) thirdLine = thirdLine.replace(" ", "");

        return (thirdLine.length() > 15 ? null : thirdLine);
    }

    private static String formatFourthLine(String fourthLine, Material material) {
        String[] split = fourthLine.split(":");
        if (uNumber.isInteger(split[0])) {
            String materialLine = material.name();
            if (split.length == 2) {
                int maxLength = (14 - split[1].length()); //15 - length - 1
                if (materialLine.length() > maxLength) materialLine = materialLine.substring(0, maxLength);
                materialLine = materialLine + ':' + split[1];
            }
            return materialLine;
        }
        return fourthLine;
    }

    private static boolean formatFirstLine(String line1, Player player) {
        return line1.isEmpty() ||
                (!line1.equals(uLongName.stripName(player.getName())) && !Permission.has(player, Permission.ADMIN));
    }

    private static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);

        Block block = event.getBlock();
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
    }
}
