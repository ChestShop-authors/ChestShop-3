package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Events.Protection.ProtectBlockEvent;
import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import me.HAklowner.SecureChests.Lock;
import me.HAklowner.SecureChests.Managers.LockManager;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class SecureChests implements Listener {
    private LockManager lockManager;

    public SecureChests() {
        lockManager = me.HAklowner.SecureChests.SecureChests.getInstance().getLockManager();
    }

    @EventHandler
    public void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }

        Block block = event.getBlock();
        Lock lock = lockManager.getLock(block.getLocation());

        if (!lock.isLocked()) {
            return;
        }

        String owner = lock.getOwner();
        if (!event.getPlayer().getName().equals(owner)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onBlockProtect(ProtectBlockEvent event) {
        if (event.isProtected()) {
            return;
        }

        Block block = event.getBlock();
        String player = event.getName();

        if (block == null || player == null) {
            return;
        }

        Lock lock = lockManager.getLock(block.getLocation());

        if (!lock.isLocked()) {
            lock.lock(player);
        }
    }
}
