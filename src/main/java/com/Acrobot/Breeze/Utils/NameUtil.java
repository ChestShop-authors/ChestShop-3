package com.Acrobot.Breeze.Utils;

import com.Acrobot.Breeze.Utils.MojangAPI.NameFetcher;
import com.Acrobot.Breeze.Utils.MojangAPI.UUIDFetcher;
import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Provides methods to handle usernames/UUIDs
 *
 * @author Andrzej Pomirski (Acrobot)
 */
public class NameUtil {
    private static final UUID INVALID_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String PROFILE_AGENT = "minecraft";

    /**
     * Get the UUID of a player
     *
     * @param player Player whose UUID to get
     * @return The player's UUID
     */
    public static UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    /**
     * Gets the UUID of a specified username (fetches it from Mojang's servers)
     *
     * @param username Username whose UUID to get
     * @return UUID of a specified username
     */
    public static UUID getUUID(String username) {
        UUIDFetcher fetcher = new UUIDFetcher(username);

        try {
            Map<String, UUID> uuidMap = fetcher.call();

            if (uuidMap.size() < 1) {
                return INVALID_UUID;
            }

            return uuidMap.get(username);
        } catch (Exception exception) {
            return INVALID_UUID;
        }
    }

    /**
     * Gets the UUID of specified usernames (fetches them from Mojang's servers)
     *
     * @param usernames Usernames whose UUID to get
     * @return UUID of the specified usernames
     */
    public static Map<String, UUID> getUUID(String... usernames) {
        UUIDFetcher fetcher = new UUIDFetcher(usernames);

        try {
            return fetcher.call();
        } catch (Exception exception) {
            return ImmutableMap.of();
        }
    }

    /**
     * Fetches a name from UUID from Mojang's servers
     *
     * @param uuid UUID to check
     * @return The name associated with an UUID
     */
    public static String getName(UUID uuid) {
        NameFetcher fetcher = new NameFetcher(uuid);

        try {
            Map<UUID, String> uuidMap = fetcher.call();

            return uuidMap.get(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetches names from UUIDs
     *
     * @param uuids UUIDs to check
     * @return Fetched names
     */
    public static Map<UUID, String> getName(UUID... uuids) {
        NameFetcher fetcher = new NameFetcher(uuids);

        try {
            return fetcher.call();
        } catch (Exception e) {
            return ImmutableMap.of();
        }
    }

    /**
     * Check if the UUID is invalid
     *
     * @param uuid UUID to check
     * @return Is the UUID invalid?
     */
    public static boolean isInvalid(UUID uuid) {
        return uuid.equals(INVALID_UUID);
    }

    /**
     * Strip the username to 15 characters (number of characters a sign can hold)
     *
     * @param username Username to strip
     * @return Stripped username
     */
    public static String stripUsername(String username) {
        return stripUsername(username, 15);
    }

    /**
     * Strips the username to a specified number of characters
     *
     * @param username Username to strip
     * @param length   Length of the expected username
     * @return Stripped username
     */
    public static String stripUsername(String username, int length) {
        if (username.length() > length) {
            return username.substring(0, length);
        }

        return username;
    }
}
