package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Restrictions.RestrictedSign;
import com.Acrobot.ChestShop.Utils.BlockSearch;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 * @author Acrobot
 */
public class blockBreak extends BlockListener {
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        boolean isAdmin = Permission.has(player, Permission.ADMIN);

        if (isAdmin) {
            return;
        }

        if (SignUtil.isSign(block)) {
            Sign currentSign = (Sign) block.getState();
            if (RestrictedSign.isRestricted(currentSign)) {
                event.setCancelled(true);
            }
            currentSign.update(true);
        }

        Sign sign = BlockSearch.findSign(block);

        if (sign != null) {
            if (!player.getName().equals(sign.getLine(0))) {
                event.setCancelled(true);
            }
        }
    }
}
