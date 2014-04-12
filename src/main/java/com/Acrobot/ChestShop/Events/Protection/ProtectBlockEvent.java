package com.Acrobot.ChestShop.Events.Protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * @author Acrobot
 */
public class ProtectBlockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Block block;

    boolean isProtected = false;

    public ProtectBlockEvent(Block block, Player player) {
        this.block = block;
        this.player = player;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean yesOrNo) {
        isProtected = yesOrNo;
    }

    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
