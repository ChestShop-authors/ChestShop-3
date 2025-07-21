package com.Acrobot.ChestShop.Events.Protection;

import com.Acrobot.ChestShop.Security;
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
    private final Block block;
    private final Security.Type type;

    boolean isProtected = false;

    public ProtectBlockEvent(Block block, Player player) {
        this(block, player, player.getUniqueId());
    }

    public ProtectBlockEvent(Block block, Player player, UUID protectionOwner) {
        this(block, player, protectionOwner, Security.Type.PRIVATE);
    }

    public ProtectBlockEvent(Block block, Player player, UUID protectionOwner, Security.Type type) {
        this.block = block;
        this.player = player;
        this.protectionOwner = protectionOwner;
        this.type = type;
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

    public Security.Type getType() {
        return type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
