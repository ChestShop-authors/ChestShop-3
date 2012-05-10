package com.Acrobot.ChestShop.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Acrobot
 */
public class BuildPermissionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Location chest, sign;

    private int disallowed = 0;
    private int received = 0;

    public BuildPermissionEvent(Player player, Location chest, Location sign) {
        this.player = player;
        this.chest = chest;
        this.sign = sign;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getChest() {
        return chest;
    }

    public Location getSign() {
        return sign;
    }

    public void allow() {
        received++;
    }

    public boolean isAllowed() {
        return disallowed != received || received == 0;
    }

    public void allow(boolean yesOrNot) {
        if (yesOrNot) {
            allow();
        } else {
            disallow();
        }
    }

    public void disallow() {
        received++;
        disallowed++;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
