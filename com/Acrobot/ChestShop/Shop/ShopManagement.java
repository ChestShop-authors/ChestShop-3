package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.Chests.MinecraftChest;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Utils.SearchForBlock;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class ShopManagement {
    public static boolean buy(Sign sign, Player player){
        Chest chestMc = SearchForBlock.findChest(sign);
        Shop shop = new Shop(chestMc != null ? new MinecraftChest(chestMc) : null, sign, Items.getItemStack(sign.getLine(3)));

        shop.buy(player);
        return true;
    }

    public static boolean sell(Sign sign, Player player){
        Chest chestMc = SearchForBlock.findChest(sign);
        Shop shop = new Shop(chestMc != null ? new MinecraftChest(chestMc) : null, sign, Items.getItemStack(sign.getLine(3)));

        shop.sell(player);
        return true;
    }
}
