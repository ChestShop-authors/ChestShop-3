package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * @author Acrobot
 */
public class ShopDestroyedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player destroyer;

    private final Sign sign;
    private final Chest chest;

    public ShopDestroyedEvent(@Nullable Player destroyer, Sign sign, @Nullable Chest chest) {
        this.destroyer = destroyer;
        this.sign = sign;
        this.chest = chest;
    }

    @Nullable public Player getDestroyer() {
        return destroyer;
    }

    @Nullable public Chest getChest() {
        return chest;
    }

    public Sign getSign() {
        return sign;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
