// Account.java
package com.Acrobot.ChestShop.Database;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.UUID;

/**
 * A mapping for an account
 */
@DatabaseTable(tableName = "accounts")
@DatabaseFileName("users.db")
public class Account {

    @DatabaseField(index = true, canBeNull = false, uniqueCombo = true)
    private String name;

    @DatabaseField(id = true, index = true, canBeNull = false)
    private String shortName;

    @DatabaseField(index = true, canBeNull = false, uniqueCombo = true)
    private UUID uuid;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_LONG, defaultValue = "0")
    private Date lastSeen;

    @DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN, defaultValue = "false")
    private boolean toggled;

    public Account() {
        //empty constructor, needed for ORMLite
    }

    public Account(String name, UUID uuid) {
        this(name, NameUtil.stripUsername(name), uuid);
    }

    public Account(String name, String shortName, UUID uuid) {
        this.name = name;
        this.shortName = shortName;
        this.uuid = uuid;
        this.toggled = false;
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

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}