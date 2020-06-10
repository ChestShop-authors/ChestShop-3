package com.Acrobot.ChestShop.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a plugin reload call
 *
 * @author Acrobot
 */
public class ChestShopReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private CommandSender sender;

    public ChestShopReloadEvent(CommandSender sender) {
        this.sender = sender;
    }

    /**
     * @return CommandSender who initiated the call
     */
    public CommandSender getSender() {
        return sender;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
