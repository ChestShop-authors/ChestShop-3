package com.Acrobot.ChestShop.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an /iteminfo call
 *
 * @author Acrobot
 */
public class ItemInfoEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSender sender;
    private final ItemStack item;

    public ItemInfoEvent(CommandSender sender, ItemStack item) {
        this.sender = sender;
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
}
