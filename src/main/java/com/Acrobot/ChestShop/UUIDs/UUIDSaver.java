package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.ChestShop;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Lets you save/cache username and UUID relations
 * @author Andrzej Pomirski (Acrobot)
 */
public class UUIDSaver {
    private static FileConfiguration uuidStorage;

    public static UUID getUUID(Player player) {
        if (uuidStorage.getString(player.getUniqueId().toString()) == null) {
            uuidStorage.set(player.getUniqueId().toString(), player.getPlayer());
        }

        return player.getUniqueId();
    }

    public static String getUsername(Player player) {
        if (uuidStorage.getString(player.getUniqueId().toString()) != null) {
            uuidStorage.set(player.getUniqueId().toString(), player.getName());
        }

        return uuidStorage.getString(player.getUniqueId().toString());
    }

    public static String getUsername(final UUID uuid) {
        String username = uuidStorage.getString(uuid.toString());

        if (username != null) {
            return username;
        }

        Bukkit.getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override
            public void run() {
                String name = NameUtil.getName(uuid);

                if (name != null) {
                    uuidStorage.set(uuid.toString(), name);
                }
            }
        });

        return uuidStorage.getString(uuid.toString());
    }

    public static void load() {
        File uuidStorageFile = ChestShop.loadFile("uuid.storage");
        uuidStorage = YamlConfiguration.loadConfiguration(uuidStorageFile);
    }

    public static void save() {
        File uuidStorageFile = ChestShop.loadFile("uuid.storage");

        try {
            uuidStorage.save(uuidStorageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
