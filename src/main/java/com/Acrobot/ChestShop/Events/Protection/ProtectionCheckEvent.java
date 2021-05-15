package com.Acrobot.ChestShop.Events.Protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Acrobot
 */
public class ProtectionCheckEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Result result = Result.DEFAULT;
    private boolean ignoreBuiltInProtection = false;
    private boolean checkManagement = true;
    private Block block;
    private Player player;

    public ProtectionCheckEvent(Block block, Player player) {
        this.block = block;
        this.player = player;
    }

    public ProtectionCheckEvent(Block block, Player player, boolean ignoreBuiltInProtection) {
        this.block = block;
        this.player = player;
        this.ignoreBuiltInProtection = ignoreBuiltInProtection;
    }

    public ProtectionCheckEvent(Block block, Player player, boolean ignoreBuiltInProtection, boolean checkManagement) {
        this.block = block;
        this.player = player;
        this.ignoreBuiltInProtection = ignoreBuiltInProtection;
        this.checkManagement = checkManagement;
    }

    public boolean isBuiltInProtectionIgnored() {
        return ignoreBuiltInProtection;
    }

    public boolean checkCanManage() {
        return checkManagement;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
