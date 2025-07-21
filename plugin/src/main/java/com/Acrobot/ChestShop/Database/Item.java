package com.Acrobot.ChestShop.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Mapping for enchanted items
 * @author Andrzej Pomirski
 */
@DatabaseTable(tableName = "items")
@DatabaseFileName("items.db")
public class Item {

    @DatabaseField(canBeNull = false, generatedId = true)
    private int id;

    @DatabaseField(columnName = "code", canBeNull = false, index = true)
    private String base64ItemCode;

    public Item() {
        //empty constructor
    }

    public Item(String base64ItemCode) {
        this.base64ItemCode = base64ItemCode;
    }

    public int getId() {
        return id;
    }

    public String getBase64ItemCode() {
        return base64ItemCode;
    }

    public void setBase64ItemCode(String base64ItemCode) {
        this.base64ItemCode = base64ItemCode;
    }
}
