package com.Acrobot.ChestShop.Utils.WorldGuard;

import com.sk89q.worldguard.protection.flags.StateFlag;

/**
 * @author Acrobot
 */
public class ChestShopFlag extends StateFlag {
    public static ChestShopFlag flag = new ChestShopFlag();

    public ChestShopFlag() {
        super("chestshop", false);
    }
}
