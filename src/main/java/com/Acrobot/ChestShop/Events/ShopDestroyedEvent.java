package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * Represents a state after shop destruction
 *
 * @author Acrobot
 */
public class ShopDestroyedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player destroyer;

    private final Sign sign;
    private final Container container;

    @Deprecated
    public ShopDestroyedEvent(@Nullable Player destroyer, Sign sign, @Nullable Chest chest) {
        this(destroyer, sign, (Container) chest);
    }

    public ShopDestroyedEvent(@Nullable Player destroyer, Sign sign, @Nullable Container container) {
        this.destroyer = destroyer;
        this.sign = sign;
        this.container = container;
    }

    /**
     * @return Shop's destroyer
     */
    @Nullable public Player getDestroyer() {
        return destroyer;
    }

    /**
     * @return Shop's chest
     */
    @Nullable public Container getContainer() {
        return container;
    }

    /**
     * @deprecated Use {@link #getContainer()}
     */
    @Deprecated
    @Nullable public Chest getChest() {
        return container instanceof Chest ? (Chest) container : null;
    }

    /**
     * @return Shop's sign
     */
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
