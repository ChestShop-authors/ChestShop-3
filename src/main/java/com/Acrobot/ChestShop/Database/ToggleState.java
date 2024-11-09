package com.Acrobot.ChestShop.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "toggle_states")
@DatabaseFileName("toggle_states.db")
public class ToggleState {

    @DatabaseField(id = true, canBeNull = false)
    private UUID playerId;

    @DatabaseField(canBeNull = false)
    private boolean toggled;

    public ToggleState() {
        // ORMLite needs a no-arg constructor
    }

    public ToggleState(UUID playerId, boolean toggled) {
        this.playerId = playerId;
        this.toggled = toggled;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}