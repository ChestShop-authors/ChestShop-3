package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * @author Acrobot
 */
public class ProtectBlockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Block block;
    private String name;

    boolean isProtected = false;

    public ProtectBlockEvent(Block block, String name) {
        this.block = block;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
