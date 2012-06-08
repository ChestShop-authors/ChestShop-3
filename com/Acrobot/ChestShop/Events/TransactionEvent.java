package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Containers.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class TransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Container container;
    private Sign sign;

    private Player client;
    private String owner;

    private ItemStack item;
    private int itemAmount;
    private double price;

    private Type transactionType;

    public TransactionEvent(Type transactionType, Container container, Sign sign, Player client, String owner, ItemStack item, int itemAmount, double price) {
        this.container = container;
        this.sign = sign;

        this.client = client;
        this.owner = owner;

        this.item = item;
        this.itemAmount = itemAmount;

        this.transactionType = transactionType;
        this.price = price;
    }

    public Type getTransactionType() {
        return transactionType;
    }

    public Container getContainer() {
        return container;
    }

    public Sign getSign() {
        return sign;
    }

    public Player getClient() {
        return client;
    }

    public String getOwner() {
        return owner;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public double getPrice() {
        return price;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum Type {
        BUY,
        SELL
    }
}
