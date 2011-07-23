package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.Chests.MinecraftChest;
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
    public static void buy(Sign sign, Player player) {
        Chest chestMc = uBlock.findChest(sign);
        ItemStack item = Items.getItemStack(sign.getLine(3));
        if (item == null) {
            player.sendMessage(ChatColor.RED + "[Shop] The item is not recognised!");
            return;
        }
        Shop shop = new Shop(chestMc != null ? new MinecraftChest(chestMc) : null, true, sign, item);
        shop.buy(player);
    }

    public static void sell(Sign sign, Player player) {
        Chest chestMc = uBlock.findChest(sign);
        ItemStack item = Items.getItemStack(sign.getLine(3));
        if (item == null) {
            player.sendMessage(ChatColor.RED + "[Shop] The item is not recognised!");
            return;
        }
        Shop shop = new Shop(chestMc != null ? new MinecraftChest(chestMc) : null, false, sign, item);
        shop.sell(player);
    }
}
