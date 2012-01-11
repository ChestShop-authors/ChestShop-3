package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Signs.restrictedSign;
import com.Acrobot.ChestShop.Utils.*;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.party.HeroParty;
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

        if (formatFirstLine(line[0], player)) event.setLine(0, uLongName.stripName(player.getName()));

        String thirdLine = formatThirdLine(line[2]);
        if (thirdLine == null) {
            dropSign(event);
            player.sendMessage(Config.getLocal(Language.YOU_CANNOT_CREATE_SHOP));
            return;
        }
        event.setLine(2, thirdLine);
        event.setLine(3, formatFourthLine(line[3], stock));

        Chest chest = uBlock.findChest(signBlock);

        boolean isAdminShop = uSign.isAdminShop(event.getLine(0));
        if (!isAdminShop) {
            if (chest == null) {
                player.sendMessage(Config.getLocal(Language.NO_CHEST_DETECTED));
                dropSign(event);
                return;
            } else if (!playerIsAdmin) {
                if (!Config.getBoolean(Property.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK) && !Security.canPlaceSign(player, (Sign) signBlock.getState())) {
                    player.sendMessage(Config.getLocal(Language.ANOTHER_SHOP_DETECTED));
                    dropSign(event);
                    return;
                }

                Block chestBlock = chest.getBlock();

                if (!uWorldGuard.isNotOutsideWGplot(signBlock.getLocation()) || (uSign.towny != null && !uTowny.canBuild(player, signBlock.getLocation(), chestBlock.getLocation()))) {
                    player.sendMessage(Config.getLocal(Language.TOWNY_CANNOT_CREATE_SHOP_HERE));
                    dropSign(event);
                    return;
                }

                boolean canAccess = !Security.isProtected(chestBlock) || Security.canAccess(player, chestBlock);
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
            if (!Security.protect(player.getName(), signBlock)) player.sendMessage(Config.getLocal(Language.NOT_ENOUGH_LWC_PROTECTIONS));
        }
        if (Config.getBoolean(Property.PROTECT_CHEST_WITH_LWC) && chest != null && Security.protect(player.getName(), chest.getBlock())) {
            player.sendMessage(Config.getLocal(Language.PROTECTED_SHOP));
        }

        uLongName.saveName(player.getName());
        player.sendMessage(Config.getLocal(Language.SHOP_CREATED) + (paid ? " - " + Economy.formatBalance(shopCreationPrice) : ""));
        if (ChestShop.heroes != null) {
            Hero hero =  ChestShop.heroes.getHeroManager().getHero(player);
            if (hero.hasParty()) {
                hero.getParty().gainExp(Config.getDouble(Property.HEROES_EXP), ExperienceType.EXTERNAL, player.getLocation());
            } else {
                hero.gainExp(Config.getDouble(Property.HEROES_EXP), ExperienceType.EXTERNAL);
            }
        }
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
        thirdLine = thirdLine.toUpperCase();
        String[] split = thirdLine.split(":");
        if (uNumber.isFloat(split[0])) thirdLine = "B " + thirdLine;
        if (split.length == 2 && uNumber.isFloat(split[1])) thirdLine = thirdLine + " S";
        if (thirdLine.length() > 15) thirdLine = thirdLine.replace(" ", "");


        return (thirdLine.length() > 15 ? null : thirdLine);
    }

    private static String formatFourthLine(String fourthLine, ItemStack is) {
        int index = (fourthLine.indexOf(':') != -1 ? fourthLine.indexOf(':') : 9999);
        if (fourthLine.indexOf('-') < index && fourthLine.indexOf('-') != -1) index = fourthLine.indexOf('-');

        StringBuilder toReturn = new StringBuilder(3);
        String matName = fourthLine.split(":|-")[0];
        matName = matName.trim();
        if (uNumber.isInteger(matName)) matName = Items.getName(is, false);
        int iPos = 15 - (fourthLine.length() - index);
        if (index != 9999 && matName.length() > iPos) matName = matName.substring(0, iPos);
        if (Items.getItemStack(matName).getType() == is.getType()) toReturn.append(matName);
        else toReturn.append(is.getTypeId());

        if (index != -1 && index != 9999) toReturn.append(fourthLine.substring(index));
        return uSign.capitalizeFirst(toReturn.toString(), ' ');
    }

    private static boolean formatFirstLine(String line1, Player player) {
        return line1.isEmpty() ||
                (!line1.equals(uLongName.stripName(player.getName()))
                && !Permission.has(player, Permission.ADMIN)
                && !Permission.otherName(player, line1));
    }

    private static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);

        Block block = event.getBlock();
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
    }
}
