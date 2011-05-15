package com.Acrobot.ChestShop.Protection;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.ProtectionTypes;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class LWCplugin implements Protection{
    public static LWC lwc;


    public boolean isProtected(Block block) {
        return lwc.findProtection(block) != null;
    }

    public boolean canAccess(Player player, Block block) {
        return lwc.canAccessProtection(player, block);
    }

    public boolean protect(String name, Block block) {
        lwc.getPhysicalDatabase().registerProtection(block.getTypeId(), ProtectionTypes.PRIVATE, block.getWorld().getName(), name, "", block.getX(), block.getY(), block.getZ());
        return true;
    }
}
