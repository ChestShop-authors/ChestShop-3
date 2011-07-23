package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Default;
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

        Boolean isAlmostReady = uSign.isValidPreparedSign(event.getLines());

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
            if (!(playerIsAdmin ||
                    Permission.has(player, Permission.SHOP_CREATION) ||
                    (Permission.has(player, Permission.SHOP_CREATION + "." + mat.getId()) &&
                            !Permission.has(player, Permission.EXCLUDE_ITEM + "." + mat.getId())))) {

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
                if (!uSign.isSign(secondSign) || !uSign.isValid((Sign) secondSign.getState())) {
                    dropSign(event);
                }
            }
            return;
        }

        Boolean isReady = uSign.isValid(line);

        if (line[0].isEmpty() || (!line[0].startsWith(player.getName()) && !Permission.has(player, Permission.ADMIN))) {
            event.setLine(0, player.getName());
        }

        line = event.getLines();
        
        boolean isAdminShop = uSign.isAdminShop(line[0]);

        if (!isReady) {
            int prices = line[2].split(":").length;
            String oldLine = line[2];
            if (prices == 1) {
                event.setLine(2, "B " + oldLine);
            } else {
                event.setLine(2, "B " + oldLine + " S");
            }
        }

        String[] split = line[3].split(":");
        if (uNumber.isInteger(split[0])) {
            String matName = mat.name();
            if (split.length == 2) {
                int length = matName.length();
                int maxLength = (15 - split[1].length() - 1);
                if (length > maxLength) {
                    matName = matName.substring(0, maxLength);
                }
                event.setLine(3, matName + ':' + split[1]);
            } else {
                event.setLine(3, matName);
            }
        }

        Chest chest = uBlock.findChest(signBlock);

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

                boolean canAccess = true;
                Block chestBlock = chest.getBlock();

                if (Security.isProtected(chestBlock) && !Security.canAccess(player, chestBlock)) {
                    canAccess = false;
                }

                if (!(Security.protection instanceof Default)) {
                    Default protection = new Default();
                    if (protection.isProtected(chestBlock) && !protection.canAccess(player, chestBlock)) {
                        canAccess = false;
                    }
                }

                if (!canAccess) {
                    player.sendMessage(Config.getLocal(Language.CANNOT_ACCESS_THE_CHEST));
                    dropSign(event);
                    return;
                }
            }
        }

        if (Config.getBoolean(Property.PROTECT_CHEST_WITH_LWC) && chest != null && Security.protect(player.getName(), chest.getBlock())) {
            if (Config.getBoolean(Property.PROTECT_SIGN_WITH_LWC)) {
                Security.protect(player.getName(), signBlock);
            }
            player.sendMessage(Config.getLocal(Language.PROTECTED_SHOP));
        }

        uLongName.saveName(player.getName());
        player.sendMessage(Config.getLocal(Language.SHOP_CREATED));
    }

    private static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);

        Block block = event.getBlock();
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
    }
}
