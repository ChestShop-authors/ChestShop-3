package com.Acrobot.ChestShop.Events;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Acrobot
 */
public class PreShopCreationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player creator;

    private CreationOutcome outcome = CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
    private Sign sign;
    private String[] signLines;

    public PreShopCreationEvent(Player creator, Sign sign, String[] signLines) {
        this.creator = creator;
        this.sign = sign;
        this.signLines = signLines.clone();
    }

    public boolean isCancelled() {
        return outcome != CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
    }

    public void setOutcome(CreationOutcome outcome) {
        this.outcome = outcome;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public void setSignLines(String[] signLines) {
        this.signLines = signLines;
    }

    public void setSignLine(byte line, String text) {
        this.signLines[line] = text;
    }

    public Player getPlayer() {
        return creator;
    }

    public Sign getSign() {
        return sign;
    }

    public String getSignLine(byte line) {
        return signLines[line];
    }

    public String[] getSignLines() {
        return signLines;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static enum CreationOutcome {
        INVALID_ITEM,
        INVALID_PRICE,
        INVALID_QUANTITY,

        SELL_PRICE_HIGHER_THAN_BUY_PRICE,

        NO_CHEST,

        NO_PERMISSION,
        NO_PERMISSION_FOR_TERRAIN,
        NO_PERMISSION_FOR_CHEST,

        NOT_ENOUGH_MONEY,

        OTHER, //For plugin use!

        SHOP_CREATED_SUCCESSFULLY
    }
}
