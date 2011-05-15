package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.Chests.ChestObject;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Shop {
    private ItemStack[] items;

    public Shop(ChestObject chest, Sign sign, ItemStack ... itemStacks){
        items = itemStacks;
    }
}
