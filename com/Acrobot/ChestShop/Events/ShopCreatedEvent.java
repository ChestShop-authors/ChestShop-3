package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Acrobot
 */
public class ShopCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Sign sign;
    private Chest chest;
    private String[] signLines;

    public ShopCreatedEvent(Player player, Sign sign, Chest chest, String[] signLines) {
        this.player = player;
        this.sign = sign;
        this.chest = chest;
        this.signLines = signLines;
    }

    public String[] getSignLines() {
        return signLines;
    }

    public Player getPlayer() {
        return player;
    }

    public Sign getSign() {
        return sign;
    }

    public Chest getChest() {
        return chest;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
