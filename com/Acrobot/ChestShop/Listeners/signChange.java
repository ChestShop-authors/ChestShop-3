package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.Security;
import com.Acrobot.ChestShop.Utils.Numerical;
import com.Acrobot.ChestShop.Utils.SearchForBlock;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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

        Boolean isAlmostReady = SignUtil.isValidPreparedSign(event.getLines());

        Player player = event.getPlayer();

        ItemStack stock = Items.getItemStack(line[3]);

        Material mat = stock == null ? null : stock.getType();

        boolean playerIsAdmin = Permission.has(player, Permission.ADMIN);


        if (isAlmostReady) {
            if (!playerIsAdmin && !(Permission.has(player, Permission.SHOP_CREATION)
                    || ((Permission.has(player, Permission.SHOP_CREATION + "." + mat.getId())
                    || !Permission.has(player, Permission.EXCLUDE_ITEM + "." + mat.getId()))))) {

                player.sendMessage(Config.getLocal("YOU_CAN'T_CREATE_SHOP"));
                dropSign(event);
                return;
            }
            if (mat == null) {
                player.sendMessage(Config.getLocal("INCORRECT_ITEM_ID"));
                dropSign(event);
                return;
            }
        } else {
            return;
        }

        Boolean isReady = SignUtil.isValid(line);

        if (line[0].isEmpty() || (!line[0].startsWith(player.getName()) && !Permission.has(player, Permission.ADMIN))) {
            event.setLine(0, player.getName());
        }

        line = event.getLines();

        boolean isAdminShop = SignUtil.isAdminShop(line[0]);

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
        if (Numerical.isInteger(split[0])) {
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

        Chest chest = SearchForBlock.findChest(signBlock);

        if (!isAdminShop) {
            if (chest == null) {
                player.sendMessage(Config.getLocal("NO_CHEST_DETECTED"));
                dropSign(event);
                return;
            } else if (!playerIsAdmin) {
                boolean canPlaceSign = Security.canPlaceSign(player, signBlock);

                if (!canPlaceSign) {
                    player.sendMessage(Config.getLocal("ANOTHER_SHOP_DETECTED"));
                    dropSign(event);
                    return;
                }
            }
        }

        if (Security.protect(player.getName(), chest.getBlock())) {
            player.sendMessage(Config.getLocal("PROTECTED_SHOP"));
        }

        player.sendMessage(Config.getLocal("SHOP_CREATED"));
    }

    public static void dropSign(SignChangeEvent event) {
        event.setCancelled(true);

        Block block = event.getBlock();
        block.setType(Material.AIR);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
    }
}
