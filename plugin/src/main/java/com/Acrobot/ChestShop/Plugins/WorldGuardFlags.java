package com.Acrobot.ChestShop.Plugins;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;

/**
 * @author Brokkonaut
 */
public class WorldGuardFlags {
    public static final StateFlag ENABLE_SHOP;

    static {
        StateFlag enableShop;
        try {
            enableShop = new StateFlag("allow-shop", false);
            WorldGuard.getInstance().getFlagRegistry().register(enableShop);
        } catch (FlagConflictException | IllegalStateException e) {
            enableShop = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("allow-shop");
        }
        ENABLE_SHOP = enableShop;
    }
}