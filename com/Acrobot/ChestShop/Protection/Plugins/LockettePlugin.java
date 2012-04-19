package com.Acrobot.ChestShop.Protection.Plugins;

import com.Acrobot.ChestShop.Protection.Protection;
import com.Acrobot.ChestShop.Utils.uLongName;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class LockettePlugin implements Protection {
    public Lockette lockette;

    public LockettePlugin(Lockette lockette) {
        this.lockette = lockette;
    }

    public boolean isProtected(Block block) {
        return Lockette.isProtected(block);
    }

    public boolean canAccess(Player player, Block block) {
        String pName = player.getName();

        String owner = Lockette.getProtectedOwner(block);
        return owner == null || owner.equals(uLongName.stripName(pName));
    }

    public boolean protect(String name, Block block) {
        return false;
    }
}
