package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.OTHER;
import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.TRANSACTION_SUCCESFUL;
import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;

/**
 * Represents a state before transaction occurs
 *
 * @author Acrobot
 */
public class PreTransactionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player client;
    private Account ownerAccount;

    private final TransactionType transactionType;
    private final Sign sign;

    private Inventory ownerInventory;
    private Inventory clientInventory;

    private ItemStack[] items;

    private BigDecimal exactPrice;

    private TransactionOutcome transactionOutcome = TRANSACTION_SUCCESFUL;

    public PreTransactionEvent(Inventory ownerInventory, Inventory clientInventory, ItemStack[] items, BigDecimal exactPrice, Player client, Account ownerAccount, Sign sign, TransactionType type) {
        this.ownerInventory = ownerInventory;
        this.clientInventory = (clientInventory == null ? client.getInventory() : clientInventory);

        this.items = items;
        this.exactPrice = exactPrice;

        this.client = client;
        this.ownerAccount = ownerAccount;

        this.sign = sign;
        this.transactionType = type;
    }

    /**
     * @deprecated Use {@link #PreTransactionEvent(Inventory, Inventory, ItemStack[], BigDecimal, Player, Account, Sign, TransactionType)}
     */
    @Deprecated
    public PreTransactionEvent(Inventory ownerInventory, Inventory clientInventory, ItemStack[] items, double price, Player client, Account ownerAccount, Sign sign, TransactionType type) {
        this(ownerInventory, clientInventory, items, BigDecimal.valueOf(price), client, ownerAccount, sign, type);
    }

    /**
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * Get the exact total price
     *
     * @return Exact total price of the items
     */
    public BigDecimal getExactPrice() {
        return exactPrice;
    }

    /**
     * Sets the exact price of the items
     *
     * @param exactPrice Price of the items
     */
    public void setExactPrice(BigDecimal exactPrice) {
        this.exactPrice = exactPrice;
    }

    /**
     * Get the total price
     *
     * @return Total price of the items
     * @deprecated Use {@link #getExactPrice()}
     */
    @Deprecated
    public double getPrice() {
        return exactPrice.doubleValue();
    }

    /**
     * Sets the price of the items
     *
     * @param price Price of the items
     * @deprecated Use {@link #setExactPrice(BigDecimal)}
     */
    @Deprecated
    public void setPrice(double price) {
        this.exactPrice = BigDecimal.valueOf(price);
    }

    /**
     * Sets the stock
     *
     * @param stock Stock
     */
    public void setStock(ItemStack... stock) {
        items = stock;
    }

    /**
     * @return Stock available
     */
    public ItemStack[] getStock() {
        return items;
    }

    /**
     * @return Shop's client
     */
    public Player getClient() {
        return client;
    }

    /**
     * @return Account of the shop's owner
     */
    public Account getOwnerAccount() {
        return ownerAccount;
    }

    /**
     * Sets the shop's owner
     *
     * @param ownerAccount Account of the shop owner
     */
    public void setOwnerAccount(Account ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    /**
     * @return Shop's owner
     * @deprecated Use {@link #getOwnerAccount}
     */
    @Deprecated
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(ownerAccount.getUuid());
    }

    /**
     * Sets the shop's owner
     *
     * @param owner Shop owner
     * @deprecated Use {@link #setOwnerAccount(Account)}
     */
    @Deprecated
    public void setOwner(OfflinePlayer owner) {
        this.ownerAccount = NameManager.getOrCreateAccount(owner);
    }

    /**
     * @return Owner's inventory
     */
    public Inventory getOwnerInventory() {
        return ownerInventory;
    }

    /**
     * Sets the owner's inventory
     *
     * @param ownerInventory Onwer's inventory
     */
    public void setOwnerInventory(Inventory ownerInventory) {
        this.ownerInventory = ownerInventory;
    }

    /**
     * Sets the client's inventory
     *
     * @param clientInventory Client's inventory
     */
    public void setClientInventory(Inventory clientInventory) {
        this.clientInventory = clientInventory;
    }

    /**
     * @return Client's inventory
     */
    public Inventory getClientInventory() {
        return clientInventory;
    }

    /**
     * @return Transaction's type
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * @return Is the transaction cancelled?
     */
    public boolean isCancelled() {
        return transactionOutcome != TRANSACTION_SUCCESFUL;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (cancel) {
            transactionOutcome = OTHER;
        } else {
            transactionOutcome = TRANSACTION_SUCCESFUL;
        }
    }

    /**
     * @return Transaction's outcome
     */
    public TransactionOutcome getTransactionOutcome() {
        return transactionOutcome;
    }

    /**
     * Sets the outcome of the transaction
     *
     * @param reason Transction's outcome
     */
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

        CLIENT_DEPOSIT_FAILED,
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
