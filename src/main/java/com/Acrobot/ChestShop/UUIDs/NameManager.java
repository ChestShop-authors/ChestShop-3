package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * Lets you save/cache username and UUID relations
 *
 * @author Andrzej Pomirski (Acrobot)
 */
@SuppressWarnings("UnusedAssignment") // I deliberately set the variables to null while initializing
public class NameManager {
    private static Dao<Account, String> accounts;

    private static Cache<String, UUID> usernameToUUID = CacheBuilder.newBuilder().maximumSize(Properties.CACHE_SIZE).build();
    private static Cache<UUID, String> uuidToUsername = CacheBuilder.newBuilder().maximumSize(Properties.CACHE_SIZE).build();
    private static Cache<String, String> shortToLongName = CacheBuilder.newBuilder().maximumSize(Properties.CACHE_SIZE).build();

    /**
     * Get the UUID from a player's (non-shortened) username
     * @param username  The player's username
     * @return          The UUID or <tt>null</tt> if the UUID can't be found or an error occurred
     */
    public static UUID getUUID(String username) {
        Validate.notEmpty(username, "username cannot be null or empty!");
        try {
            return usernameToUUID.get(username, () -> {
                UUID uuid = null;
                Player player = Bukkit.getPlayer(username);
                if (player != null) {
                    uuid = player.getUniqueId();
                }
                if (uuid == null) {
                    try {
                        Account account = accounts.queryBuilder().selectColumns("uuid").where().eq("lastSeenName", username).queryForFirst();
                        if (account != null) {
                            uuid = account.getUuid();
                        }
                    } catch (SQLException e) {
                        Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error while getting uuid for " + username + ":", e);
                    }
                }
                if (uuid == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
                    if (offlinePlayer != null && offlinePlayer.hasPlayedBefore() && offlinePlayer.getUniqueId() != null) {
                        uuid = offlinePlayer.getUniqueId();
                    }
                }
                if (uuid != null) {
                    uuidToUsername.put(uuid, username);
                    return uuid;
                }
                throw new Exception("Could not find username for " + uuid);
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Get the username from a player's UUID
     * @param uuid  The UUID of the player
     * @return      The username that is stored, an empty string if none was found or <tt>null</tt> if an error occurred
     */
    public static String getUsername(UUID uuid) {
        try {
            return uuidToUsername.get(uuid, () -> {
                String name = null;
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    name = player.getName();
                }
                if (name == null) {
                    try {
                        Account account = accounts.queryBuilder().selectColumns("lastSeenName").where().eq("uuid", uuid).queryForFirst();
                        if (account != null) {
                            name = account.getLastSeenName();
                        }
                    } catch (SQLException e) {
                        Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error while getting username for " + uuid + ":", e);
                    }
                }
                if (name == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (offlinePlayer != null && offlinePlayer.hasPlayedBefore() && offlinePlayer.getName() != null) {
                        name = offlinePlayer.getName();
                    }
                }
                if (name != null) {
                    usernameToUUID.put(name, uuid);
                    return name;
                }
                throw new Exception("Could not find username for " + uuid);
            });
        } catch (ExecutionException e) {
            return "";
        }
    }

    /**
     * Get the full username from another username that might be shortened
     * @param shortName The name of the player to get the full username for
     * @return          The full username, an empty string if none was found or <tt>null</tt> if an error occurred
     * @throws IllegalArgumentException if the username is not a shortened name and longer than 15 chars
     */
    public static String getFullUsername(String shortName) {
        Validate.isTrue(shortName.length() < 16, "Username is not a shortened name and longer than 15 chars!");
        if (ChestShopSign.isAdminShop(shortName)) {
            return Properties.ADMIN_SHOP_NAME;
        }

        try {
            return shortToLongName.get(shortName, () -> {
                try {
                    Account account = accounts.queryBuilder().selectColumns("lastSeenName").where().eq("shortName", shortName).queryForFirst();
                    if (account != null) {
                        return account.getLastSeenName();
                    }
                } catch (SQLException e) {
                    Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error while getting full name for " + shortName + ":", e);
                }
                throw new Exception("Could not find full name for " + shortName);
            });
        } catch (ExecutionException ignored) {
            return "";
        }
    }

    /**
     * Store the username of a player into the database and the username-uuid cache
     * @param player    The data transfer object of the player to store
     */
    public static void storeUsername(final PlayerDTO player) {
        final UUID uuid = player.getUniqueId();

        CloseableIterator<Account> existingAccounts = null;
        try {
            existingAccounts = accounts.queryBuilder().where().eq("uuid", uuid).ne("lastSeenName", player.getName()).iterator();
            while (existingAccounts.hasNext()) {
                Account account = existingAccounts.next();
                account.setUuid(uuid); //HOW IS IT EVEN POSSIBLE THAT UUID IS NOT SET EVEN IF WE HAVE FOUND THE PLAYER?!
                account.setLastSeenName(player.getName());
                try {
                    accounts.update(account);
                } catch (SQLException e) {
                    Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error while updating account " + account + ":", e);
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error getting all entries for " + uuid + ":", e);
            return;
        } finally {
            if (existingAccounts != null) {
                try {
                    existingAccounts.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        Account latestAccount = null;
        try {
            latestAccount = accounts.queryBuilder().where().eq("uuid", uuid).eq("name", player.getName()).queryForFirst();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error while searching for latest account of " + player.getName() + "/" + uuid + ":", e);
        }

        if (latestAccount == null) {
            latestAccount = new Account(player.getName(), player.getUniqueId());
            latestAccount.setShortName(getNewShortenedName(player));

            try {
                accounts.create(latestAccount);
            } catch (SQLException e) {
                Bukkit.getLogger().log(Level.WARNING, "[ChestShop] Error while updating account " + latestAccount + ":", e);
            }
        }

        usernameToUUID.put(latestAccount.getLastSeenName(), uuid);
        uuidToUsername.put(uuid, latestAccount.getLastSeenName());

        shortToLongName.put(latestAccount.getShortName(), latestAccount.getLastSeenName());
    }

    /**
     * Get a new unique shortened name that hasn't been used by another player yet
     * @param player    The player data to get the shortened name for
     * @return          A new shortened name that hasn't been used before and is a maximum of 15 chars long
     */
    private static String getNewShortenedName(PlayerDTO player) {
        String shortenedName = NameUtil.stripUsername(player.getName());

        String fullName = getFullUsername(shortenedName);
        if (fullName != null && fullName.isEmpty()) {
            return shortenedName;
        }
        for (int id = 0; fullName != null && !fullName.isEmpty(); id++) {
            String baseId = Base62.encode(id);
            shortenedName = NameUtil.stripUsername(player.getName(), 15 - 1 - baseId.length()) + ":" + baseId;
            fullName = getFullUsername(shortenedName);
        }

        return shortenedName;
    }

    public static boolean canUseName(Player player, String name) {
        String shortenedName = NameUtil.stripUsername(getUsername(player.getUniqueId()));

        if (ChestShopSign.isAdminShop(name)) {
            return false;
        }

        return shortenedName.equals(name) || Permission.otherName(player, name) || (!name.isEmpty() && player.getName().equals(getFullUsername(name)));
    }

    public static boolean isAdminShop(UUID uuid) {
        return Properties.ADMIN_SHOP_NAME.equals(getUsername(uuid));
    }

    public static void load() {
        try {
            accounts = DaoCreator.getDaoAndCreateTable(Account.class);

            Account adminAccount = new Account(Properties.ADMIN_SHOP_NAME, Bukkit.getOfflinePlayer(Properties.ADMIN_SHOP_NAME).getUniqueId());
            accounts.createOrUpdate(adminAccount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
