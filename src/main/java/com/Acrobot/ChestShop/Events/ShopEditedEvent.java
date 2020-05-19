package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents an event caused by a shop modification
 *
 * @author Andrzej Pomirski
 * @deprecated - not yet implemented
 * TODO: Implement
 */
public class ShopEditedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player modifier;
    private final Sign sign;
    private final String[] signLines;

    public ShopEditedEvent(Player modifier, Sign sign, String[] signLines) {
        this.modifier = modifier;
        this.sign = sign;
        this.signLines = signLines;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return The person who initiated the event
     */
    public Player getModifier() {
        return modifier;
    }

    /**
     * @return Shop sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * @return Shop sign text
     */
    public String[] getSignLines() {
        return signLines;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
