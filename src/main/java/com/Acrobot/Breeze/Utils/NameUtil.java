package com.Acrobot.Breeze.Utils;

import com.Acrobot.Breeze.Utils.MojangAPI.NameFetcher;
import com.Acrobot.Breeze.Utils.MojangAPI.UUIDFetcher;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Provides methods to handle usernames/UUIDs
 * @author Andrzej Pomirski (Acrobot)
 */
public class NameUtil {
    private static final UUID INVALID_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String PROFILE_AGENT = "minecraft";

    public static UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    public static UUID getUUID(String username) {
        UUIDFetcher fetcher = new UUIDFetcher(username);

        try {
            Map<String, UUID> uuidMap = fetcher.call();

            if (uuidMap.size() < 1) {
                return INVALID_UUID;
            }

            return uuidMap.get(username);
        } catch (Exception exception){
            return INVALID_UUID;
        }
    }

    public static Map<String, UUID> getUUID(String... usernames) {
        UUIDFetcher fetcher = new UUIDFetcher(usernames);

        try {
            return fetcher.call();
        } catch (Exception exception){
            return ImmutableMap.of();
        }
    }

    public static String getName(UUID uuid) {
        NameFetcher fetcher = new NameFetcher(uuid);

        try {
            Map<UUID, String> uuidMap = fetcher.call();

            return uuidMap.get(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<UUID, String> getName(UUID... uuids) {
        NameFetcher fetcher = new NameFetcher(uuids);

        try {
            return fetcher.call();
        } catch (Exception e) {
            return ImmutableMap.of();
        }
    }

    public static boolean isInvalid(UUID uuid) {
        return uuid.equals(INVALID_UUID);
    }
}
