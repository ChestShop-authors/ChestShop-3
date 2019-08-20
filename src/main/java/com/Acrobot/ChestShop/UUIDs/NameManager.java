package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.Breeze.Collection.SimpleCache;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.j256.ormlite.dao.Dao;

import com.j256.ormlite.stmt.SelectArg;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static com.Acrobot.ChestShop.Permission.OTHER_NAME;

/**
 * Lets you save/cache username and UUID relations
 *
 * @author Andrzej Pomirski (Acrobot)
 */
@SuppressWarnings("UnusedAssignment") // I deliberately set the variables to null while initializing
public class NameManager implements Listener {
    private static Dao<Account, String> accounts;

    private static SimpleCache<String, Account> usernameToAccount = new SimpleCache<>(Properties.CACHE_SIZE);
    private static SimpleCache<UUID, Account> uuidToAccount = new SimpleCache<>(Properties.CACHE_SIZE);
    private static SimpleCache<String, Account> shortToAccount = new SimpleCache<>(Properties.CACHE_SIZE);
    private static SimpleCache<String, Boolean> invalidPlayers = new SimpleCache<>(Properties.CACHE_SIZE);

    private static Account adminAccount;
    private static Account serverEconomyAccount;
    private static int uuidVersion = -1;

    /**
     * Get or create an account for a player
     *
     * @param player The Player to get or create the account for (only if the player has both an UUID and a name!)
     * @return The account info
     * @throws IllegalArgumentException when an invalid player object was passed
     */
    public static Account getOrCreateAccount(OfflinePlayer player) {
        Validate.notNull(player.getName(), "Name of player is null?");
        Validate.notNull(player.getUniqueId(), "UUID of player is null?");
        Validate.isTrue(player.getUniqueId().version() == uuidVersion, "Invalid OfflinePlayer! " + player.getUniqueId() + " is not of server version " + uuidVersion);

        Account account = getAccount(player.getUniqueId());
        if (account == null) {
            account = storeUsername(new PlayerDTO(player.getUniqueId(), player.getName()));
        }
        return account;
    }

    /**
     * Get account info from a UUID
     *
     * @param uuid The UUID of the player to get the account info
     * @return The account info or <tt>null</tt> if none was found
     */
    public static Account getAccount(UUID uuid) {
        try {
            return uuidToAccount.get(uuid, () -> {
                try {
                    Account account = accounts.queryBuilder().orderBy("lastSeen", false).where().eq("uuid", new SelectArg(uuid)).queryForFirst();
                    if (account != null) {
                        account.setUuid(uuid); // HOW IS IT EVEN POSSIBLE THAT UUID IS NOT SET EVEN IF WE HAVE FOUND THE PLAYER?!
                        shortToAccount.put(account.getShortName(), account);
                        usernameToAccount.put(account.getName(), account);
                        return account;
                    }
                } catch (SQLException e) {
                    ChestShop.getBukkitLogger().log(Level.WARNING, "Error while getting account for " + uuid + ":", e);
                }
                throw new Exception("Could not find account for " + uuid);
            });
        } catch (ExecutionException ignored) {
            return null;
        }
    }

    /**
     * Get account info from a non-shortened username
     *
     * @param fullName The full name of the player to get the account info
     * @return The account info or <tt>null</tt> if none was found
     * @throws IllegalArgumentException if the username is empty or null
     */
    public static Account getAccount(String fullName) {
        Validate.notEmpty(fullName, "fullName cannot be null or empty!");
        try {
            return usernameToAccount.get(fullName, () -> {
                try {
                    Account account = accounts.queryBuilder().orderBy("lastSeen", false).where().eq("name", new SelectArg(fullName)).queryForFirst();
                    if (account != null) {
                        account.setName(fullName); // HOW IS IT EVEN POSSIBLE THAT THE NAME IS NOT SET EVEN IF WE HAVE FOUND THE PLAYER?!
                        shortToAccount.put(account.getShortName(), account);
                        return account;
                    }
                } catch (SQLException e) {
                    ChestShop.getBukkitLogger().log(Level.WARNING, "Error while getting account for " + fullName + ":", e);
                }
                throw new Exception("Could not find account for " + fullName);
            });
        } catch (ExecutionException ignored) {
            return null;
        }
    }

    @EventHandler
    public static void onAccountQuery(AccountQueryEvent event) {
        if (event.getAccount() == null) {
            event.setAccount(getLastAccountFromShortName(event.getName()));
        }
    }

    /**
     * Get account info from a username that might be shortened.
     * If no account was found it will try to search all known players and create an account.
     *
     * @param shortName The name of the player to get the account info
     * @return The account info or <tt>null</tt> if none was found
     * @throws IllegalArgumentException if the username is empty
     * @deprecated Use the {@link AccountQueryEvent} instead!
     */
    public static Account getAccountFromShortName(String shortName) {
        return getAccountFromShortName(shortName, true);
    }

    private static Account getAccountFromShortName(String shortName, boolean searchOfflinePlayer) {

        Validate.notEmpty(shortName, "shortName cannot be null or empty!");
        Account account = null;

        if (shortName.length() > 15) {
            account = getAccount(shortName);
        } else {
            try {
                account = shortToAccount.get(shortName, () -> {
                    try {
                        Account a = accounts.queryBuilder().where().eq("shortName", new SelectArg(shortName)).queryForFirst();
                        if (a != null) {
                            a.setShortName(shortName); // HOW IS IT EVEN POSSIBLE THAT THE NAME IS NOT SET EVEN IF WE HAVE FOUND THE PLAYER?!
                            return a;
                        }
                    } catch (SQLException e) {
                        ChestShop.getBukkitLogger().log(Level.WARNING, "Error while getting account for " + shortName + ":", e);
                    }
                    throw new Exception("Could not find account for " + shortName);
                });
            } catch (ExecutionException ignored) {}
        }

        if (account == null && searchOfflinePlayer && !invalidPlayers.contains(shortName.toLowerCase())) {
            // no account with that shortname was found, try to get an offline player with that name
            OfflinePlayer offlinePlayer = ChestShop.getBukkitServer().getOfflinePlayer(shortName);
            if (offlinePlayer != null && offlinePlayer.getName() != null && offlinePlayer.getUniqueId() != null
                    && offlinePlayer.getUniqueId().version() == uuidVersion) {
                account = storeUsername(new PlayerDTO(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
            } else {
                invalidPlayers.put(shortName.toLowerCase(), true);
            }
        }
        return account;
    }

    /**
     * Get the information from the last time a player logged in that previously used the shortened name
     *
     * @param shortName The name of the player to get the last account for
     * @return The last account or <tt>null</tt> if none was found
     * @throws IllegalArgumentException if the username is empty
     * @deprecated Use the {@link AccountQueryEvent} instead!
     */
    @Deprecated
    public static Account getLastAccountFromShortName(String shortName) {
        Account account = getAccountFromShortName(shortName); // first get the account associated with the short name
        if (account != null) {
            return getAccount(account.getUuid()); // then get the last account that was online with that UUID
        }
        return null;
    }

    /**
     * Get the UUID from a player's (non-shortened) username
     *
     * @param username The player's username
     * @return The UUID or <tt>null</tt> if the UUID can't be found or an error occurred
     * @deprecated Use {@link NameManager#getAccount(String)}
     */
    @Deprecated
    public static UUID getUUID(String username) {
        Validate.notEmpty(username, "username cannot be null or empty!");
        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            return player.getUniqueId();
        }
        Account account = getAccount(username);
        if (account != null) {
            return account.getUuid();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore() && offlinePlayer.getUniqueId() != null) {
            return offlinePlayer.getUniqueId();
        }
        return null;
    }

    /**
     * Get the username from a player's UUID
     *
     * @param uuid The UUID of the player
     * @return The username that is stored or <tt>null</tt> if none was found
     * @deprecated Use {@link NameManager#getAccount(UUID)}
     */
    @Deprecated
    public static String getUsername(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }
        Account account = getAccount(uuid);
        if (account != null) {
            return account.getName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore() && offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        }
        return null;
    }

    /**
     * Get the full username from another username that might be shortened
     *
     * @param shortName The name of the player to get the full username for
     * @return The full username or <tt>null</tt> if none was found
     * @throws IllegalArgumentException if the username is not a shortened name and longer than 15 chars
     * @deprecated Use {@link NameManager#getAccountFromShortName(String)}
     */
    @Deprecated
    public static String getFullUsername(String shortName) {
        AccountQueryEvent accountQueryEvent = new AccountQueryEvent(shortName);
        Bukkit.getPluginManager().callEvent(accountQueryEvent);
        Account account = accountQueryEvent.getAccount();
        if (account != null) {
            return account.getName();
        }
        return null;
    }

    /**
     * Get the short username from a full username
     *
     * @param fullName The name of the player to get the short username for
     * @return The short username or <tt>null</tt> if none was found
     * @deprecated Use {@link NameManager#getAccount(String)}
     */
    @Deprecated
    public static String getShortUsername(String fullName) {
        Account account = getAccount(fullName);
        return account != null ? account.getShortName() : null;
    }

    /**
     * Store the username of a player into the database and the username-uuid cache
     *
     * @param player The data transfer object of the player to store
     * @return The stored/updated account. <tt>null</tt> if there was an error updating it
     */
    public static Account storeUsername(final PlayerDTO player) {
        final UUID uuid = player.getUniqueId();

        Account latestAccount = null;
        try {
            latestAccount = accounts.queryBuilder().where().eq("uuid", new SelectArg(uuid)).and().eq("name", new SelectArg(player.getName())).queryForFirst();
        } catch (SQLException e) {
            ChestShop.getBukkitLogger().log(Level.WARNING, "Error while searching for latest account of " + player.getName() + "/" + uuid + ":", e);
        }

        if (latestAccount == null) {
            latestAccount = new Account(player.getName(), getNewShortenedName(player), player.getUniqueId());
        }

        latestAccount.setLastSeen(new Date());
        try {
            accounts.createOrUpdate(latestAccount);
        } catch (SQLException e) {
            ChestShop.getBukkitLogger().log(Level.WARNING, "Error while updating account " + latestAccount + ":", e);
            return null;
        }

        usernameToAccount.put(latestAccount.getName(), latestAccount);
        uuidToAccount.put(uuid, latestAccount);
        shortToAccount.put(latestAccount.getShortName(), latestAccount);

        return latestAccount;
    }

    /**
     * Get a new unique shortened name that hasn't been used by another player yet
     *
     * @param player The player data to get the shortened name for
     * @return A new shortened name that hasn't been used before and is a maximum of 15 chars long
     */
    private static String getNewShortenedName(PlayerDTO player) {
        String shortenedName = NameUtil.stripUsername(player.getName());

        Account account = getAccountFromShortName(shortenedName, false);
        if (account == null) {
            return shortenedName;
        }
        for (int id = 0; account != null; id++) {
            String baseId = Base62.encode(id);
            shortenedName = NameUtil.stripUsername(player.getName(), 15 - 1 - baseId.length()) + ":" + baseId;
            account = getAccountFromShortName(shortenedName, false);
        }

        return shortenedName;
    }

    /**
     * @deprecated Use {@link #canUseName(Player, Permission, String)} to provide specific information about how the player wants to use the name
     */
    @Deprecated
    public static boolean canUseName(Player player, String name) {
        return canUseName(player, OTHER_NAME, name);
    }

    public static boolean canUseName(Player player, Permission base, String name) {
        if (ChestShopSign.isAdminShop(name)) {
            return Permission.has(player, Permission.ADMIN_SHOP);
        }

        if (Permission.otherName(player, base, name)) {
            return true;
        }

        Account account = getAccountFromShortName(name, false);
        return account != null && (account.getUuid().equals(player.getUniqueId())
                || (!account.getName().equalsIgnoreCase(name) && Permission.otherName(player, base, account.getName())));
    }

    public static boolean isAdminShop(UUID uuid) {
        return adminAccount != null && uuid.equals(adminAccount.getUuid());
    }

    public static boolean isServerEconomyAccount(UUID uuid) {
        return serverEconomyAccount != null && uuid.equals(serverEconomyAccount.getUuid());
    }

    public static void load() {
        if (getUuidVersion() < 0 && !Bukkit.getOnlinePlayers().isEmpty()) {
            setUuidVersion(Bukkit.getOnlinePlayers().iterator().next().getUniqueId().version());
        }
        try {
            accounts = DaoCreator.getDaoAndCreateTable(Account.class);

            adminAccount = new Account(Properties.ADMIN_SHOP_NAME, Bukkit.getOfflinePlayer(Properties.ADMIN_SHOP_NAME).getUniqueId());
            accounts.createOrUpdate(adminAccount);

            if (!Properties.SERVER_ECONOMY_ACCOUNT.isEmpty()) {
                serverEconomyAccount = getAccount(Properties.SERVER_ECONOMY_ACCOUNT);
                if (serverEconomyAccount == null || serverEconomyAccount.getUuid() == null) {
                    serverEconomyAccount = null;
                    ChestShop.getBukkitLogger().log(Level.WARNING, "Server economy account setting '" + Properties.SERVER_ECONOMY_ACCOUNT + "' doesn't seem to be the name of a known player! Please log in at least once in order for the server economy account to work.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Account getServerEconomyAccount() {
        return serverEconomyAccount;
    }

    public static void setUuidVersion(int uuidVersion) {
        NameManager.uuidVersion = uuidVersion;
    }

    public static int getUuidVersion() {
        return uuidVersion;
    }
}
