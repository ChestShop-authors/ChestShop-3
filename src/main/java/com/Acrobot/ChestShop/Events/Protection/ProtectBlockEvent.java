package com.Acrobot.ChestShop.Events.Protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;


/**
 * @author Acrobot
 */
public class ProtectBlockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final UUID protectionOwner;
    private Block block;

    boolean isProtected = false;

    public ProtectBlockEvent(Block block, Player player) {
        this(block, player, player.getUniqueId());
    }

    public ProtectBlockEvent(Block block, Player player, UUID protectionOwner) {
        this.block = block;
        this.player = player;
        this.protectionOwner = protectionOwner;
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

    public UUID getProtectionOwner() {
        return protectionOwner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
