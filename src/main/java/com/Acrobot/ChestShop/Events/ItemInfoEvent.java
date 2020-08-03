package com.Acrobot.ChestShop.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an /iteminfo call
 *
 * @author Acrobot
 */
public class ItemInfoEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private CommandSender sender;
    private ItemStack item;
    private boolean cancelled;

    public ItemInfoEvent(CommandSender sender, ItemStack item) {
        this.sender = sender;
        this.item = item;
        this.cancelled = false;
    }

    /**
     * @return CommandSender who initiated the call
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return Item recognised by /iteminfo
     */
    public ItemStack getItem() {
        return item;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled () {
        return cancelled;
    }
    
    @Override
    public void setCancelled (boolean b) {
        cancelled = b;
    }
}
