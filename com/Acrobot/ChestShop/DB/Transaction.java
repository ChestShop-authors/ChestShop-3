package com.Acrobot.ChestShop.DB;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Acrobot
 */
@Entity()
@Table(name = "cs_transactions")
public class Transaction {

    @Id
    private int id;

    private boolean buy;
    private String shopOwner;
    private String shopUser;
    private int itemID;
    private int itemDurability;
    private int amount;
    private float price;
    private long sec;

    public Transaction() {}

    public float getAveragePricePerItem() {
        return price / amount;
    }

    public int getId() {
        return id;
    }

    public boolean isBuy() {
        return buy;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public String getShopUser() {
        return shopUser;
    }

    public int getItemID() {
        return itemID;
    }

    public int getItemDurability() {
        return itemDurability;
    }

    public int getAmount() {
        return amount;
    }

    public float getPrice() {
        return price;
    }

    public long getSec() {
        return sec;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public void setShopOwner(String shopOwner) {
        this.shopOwner = shopOwner;
    }

    public void setShopUser(String shopUser) {
        this.shopUser = shopUser;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setItemDurability(int itemDurability) {
        this.itemDurability = itemDurability;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setSec(long sec) {
        this.sec = sec;
    }
}
