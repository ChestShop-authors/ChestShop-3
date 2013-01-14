package com.Acrobot.ChestShop.Events;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.TRANSACTION_SUCCESFUL;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;

/**
 * @author Acrobot
 */
public class PreTransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player client;
    private OfflinePlayer owner;

    private final TransactionType transactionType;
    private final Sign sign;

    private Inventory ownerInventory;
    private Inventory clientInventory;

    private ItemStack[] items;
    private double price;

    private TransactionOutcome transactionOutcome = TRANSACTION_SUCCESFUL;

    public PreTransactionEvent(Inventory ownerInventory, Inventory clientInventory, ItemStack[] items, double price, Player client, OfflinePlayer owner, Sign sign, TransactionType type) {
        this.ownerInventory = ownerInventory;
        this.clientInventory = (clientInventory == null ? client.getInventory() : clientInventory);

        this.items = items;
        this.price = price;

        this.client = client;
        this.owner = owner;

        this.sign = sign;
        this.transactionType = type;
    }

    public Sign getSign() {
        return sign;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(ItemStack... stock) {
        items = stock;
    }

    public ItemStack[] getStock() {
        return items;
    }

    public Player getClient() {
        return client;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
    }

    public Inventory getOwnerInventory() {
        return ownerInventory;
    }

    public void setOwnerInventory(Inventory ownerInventory) {
        this.ownerInventory = ownerInventory;
    }

    public void setClientInventory(Inventory clientInventory) {
        this.clientInventory = clientInventory;
    }

    public Inventory getClientInventory() {
        return clientInventory;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public boolean isCancelled() {
        return transactionOutcome != TRANSACTION_SUCCESFUL;
    }

    public TransactionOutcome getTransactionOutcome() {
        return transactionOutcome;
    }

    public void setCancelled(TransactionOutcome reason) {
        transactionOutcome = reason;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum TransactionOutcome {
        SHOP_DOES_NOT_BUY_THIS_ITEM,
        SHOP_DOES_NOT_SELL_THIS_ITEM,

        CLIENT_DOES_NOT_HAVE_PERMISSION,

        CLIENT_DOES_NOT_HAVE_ENOUGH_MONEY,
        SHOP_DOES_NOT_HAVE_ENOUGH_MONEY,
        /** not enough space in shop's account for money deposit on sell */
        SHOP_DEPOSIT_FAILED,

        NOT_ENOUGH_SPACE_IN_CHEST,
        NOT_ENOUGH_SPACE_IN_INVENTORY,

        NOT_ENOUGH_STOCK_IN_CHEST,
        NOT_ENOUGH_STOCK_IN_INVENTORY,

        INVALID_SHOP,

        SPAM_CLICKING_PROTECTION,
        CREATIVE_MODE_PROTECTION,
        SHOP_IS_RESTRICTED,

        OTHER, //For plugin use!

        TRANSACTION_SUCCESFUL
    }
}
