package com.Acrobot.ChestShop.Plugins;
import com.sk89q.worldguard.protection.flags.Flags;
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
            enableShop = Flags.register(new StateFlag("allow-shop", false));
        } catch (FlagConflictException | IllegalStateException e) {
            enableShop = (StateFlag) Flags.get("allow-shop");
        }
        ENABLE_SHOP = enableShop;
    }
}