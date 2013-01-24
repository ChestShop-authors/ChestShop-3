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
public class ShopCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player creator;

    private final Sign sign;
    private final String[] signLines;
    @Nullable private final Chest chest;

    public ShopCreatedEvent(Player creator, Sign sign, @Nullable Chest chest, String[] signLines) {
        this.creator = creator;
        this.sign = sign;
        this.chest = chest;
        this.signLines = signLines.clone();
    }

    public String getSignLine(short line) {
        return signLines[line];
    }

    public String[] getSignLines() {
        return signLines;
    }

    public Player getPlayer() {
        return creator;
    }

    public Sign getSign() {
        return sign;
    }

    @Nullable public Chest getChest() {
        return chest;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
