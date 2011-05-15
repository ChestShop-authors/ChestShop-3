package com.Acrobot.iConomyChestShop.Shop;

import com.Acrobot.iConomyChestShop.Chests.ChestObject;
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
