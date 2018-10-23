package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * Represents a state after shop creation
 *
 * @author Acrobot
 */
public class ShopCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player creator;

    private final Sign sign;
    private final String[] signLines;
    @Nullable private final Container container;

    @Deprecated
    public ShopCreatedEvent(Player creator, Sign sign, @Nullable Chest chest, String[] signLines) {
        this(creator, sign, (Container) chest, signLines);
    }

    public ShopCreatedEvent(Player creator, Sign sign, @Nullable Container container, String[] signLines) {
        this.creator = creator;
        this.sign = sign;
        this.container = container;
        this.signLines = signLines.clone();
    }

    /**
     * Returns the text on the sign
     *
     * @param line Line number (0-3)
     * @return Text on the sign
     */
    public String getSignLine(short line) {
        return signLines[line];
    }

    /**
     * Returns the text on the sign
     *
     * @return Text on the sign
     */
    public String[] getSignLines() {
        return signLines;
    }

    /**
     * Returns the shop's creator
     *
     * @return Shop's creator
     */
    public Player getPlayer() {
        return creator;
    }

    /**
     * Returns the shop's sign
     *
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * Returns the shop's container (if applicable)
     *
     * @return Shop's container
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

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
