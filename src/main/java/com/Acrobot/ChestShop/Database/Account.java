package com.Acrobot.ChestShop.Database;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * A mapping for an account
 * @author Andrzej Pomirski (Acrobot)
 */
@DatabaseTable(tableName = "accounts")
public class Account {

    @DatabaseField(id = true)
    private String name;

    @DatabaseField(index = true)
    private String shortName;

    @DatabaseField
    private UUID uuid;

    public Account() {
        //empty constructor, needed for ORMLite
    }

    public Account(String name, UUID uuid) {
        this.name = name;
        this.shortName = NameUtil.stripUsername(name);
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
