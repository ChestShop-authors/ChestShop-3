package com.Acrobot.ChestShop.UUIDs;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Data Transfer Object for Player objects
 *
 * Since Bukkit API is not thread-safe, this should work
 * @author Andrzej Pomirski
 */
public class PlayerDTO {
    private UUID uniqueId;
    private String name;

    public PlayerDTO(UUID uuid, String name) {
        this.uniqueId = uuid;
        this.name = name;
    }

    public PlayerDTO(Player player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
