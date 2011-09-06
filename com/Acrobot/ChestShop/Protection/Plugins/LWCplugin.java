package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Protection.Protection;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.ProtectionTypes;
import com.griefcraft.modules.limits.LimitsModule;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class LWCplugin implements Protection {
    public static LWC lwc;
    private static LimitsModule limitsModule;


    public static void setLWC(LWC LWC) {
        lwc = LWC;
        limitsModule = new LimitsModule();
    }

    public boolean isProtected(Block block) {
        return lwc.findProtection(block) != null;
    }

    public boolean canAccess(Player player, Block block) {
        return lwc.canAccessProtection(player, block);
    }

    public boolean protect(String name, Block block) {
        if (lwc.findProtection(block) != null) return false;
        Player player = ChestShop.getBukkitServer().getPlayer(name);
        try {
            if (player != null && limitsModule.hasReachedLimit(player, block)) return false;
        } catch (NoSuchMethodError e) {
            System.out.println(ChestShop.chatPrefix + "Your LWC plugin is outdated!");
        }

        lwc.getPhysicalDatabase().registerProtection(block.getTypeId(), ProtectionTypes.PRIVATE, block.getWorld().getName(), name, "", block.getX(), block.getY(), block.getZ());
        return true;
    }
}
