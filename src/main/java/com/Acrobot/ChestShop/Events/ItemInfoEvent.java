package com.Acrobot.ChestShop.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class ItemInfoEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private CommandSender sender;
    private ItemStack item;

    public ItemInfoEvent(CommandSender sender, ItemStack item) {
        this.sender = sender;
        this.item = item;
    }

    public CommandSender getSender() {
        return sender;
    }

    public ItemStack getItem() {
        return item;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
