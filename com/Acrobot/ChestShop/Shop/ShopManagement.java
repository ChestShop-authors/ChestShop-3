package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.Containers.MinecraftChest;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class ShopManagement {
    public static boolean useOldChest = false;

    public static void buy(Sign sign, Player player) {
        Shop shop = getShop(sign, player);
        if (shop != null) {
            shop.buy(player);
        }
    }

    public static void sell(Sign sign, Player player) {
        Shop shop = getShop(sign, player);
        if (shop != null) {
            shop.sell(player);
        }
    }

    public static Shop getShop(Sign sign, Player player) {
        Chest chestMc = uBlock.findChest(sign);
        ItemStack item = Items.getItemStack(sign.getLine(3));
        if (item == null) {
            player.sendMessage(ChatColor.RED + "[Shop] The item is not recognised!");
            return null;
        }
        return new Shop(chestMc != null ? new MinecraftChest(chestMc) : null, sign, item);
    }
}
