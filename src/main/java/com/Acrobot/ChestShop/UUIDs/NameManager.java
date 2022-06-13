package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.Breeze.Collection.SimpleCache;
import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Events.AccountAccessEvent;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.Dao;

import com.j256.ormlite.stmt.SelectArg;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

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

    public static int getAccountCount() {
        try {
            return NumberUtil.toInt(accounts.queryBuilder().countOf() - 1);
        } catch (SQLException e) {
            return 0;
        }
    }

    /**
     * Get or create an account for a player
     *
     * @param player The Player to get or create the account for (only if the player has both an UUID and a name!)
     * @return The account info
     * @throws IllegalArgumentException when an invalid player object was passed
     */
    public static Account getOrCreateAccount(OfflinePlayer player) {
        Preconditions.checkNotNull(player.getName(), "Name of player " + player.getUniqueId() + " is null?");
        Preconditions.checkArgument(!(player instanceof Player) || !Properties.ENSURE_CORRECT_PLAYERID || uuidVersion < 0 || player.getUniqueId().version() == uuidVersion,
                "Invalid OfflinePlayer! " + player.getUniqueId() + " has version " + player.getUniqueId().version() + " and not server version " + uuidVersion + ". " +
                        "If you believe that is an error and your setup allows such UUIDs then set the ENSURE_CORRECT_PLAYERID config option to false.");
        return getOrCreateAccount(player.getUniqueId(), player.getName());
    }

    /**
     * Get or create an account for a player
     *
     * @param id The UUID of the player to get or create the account for
     * @param name The name of the player to get or create the account fo
     * @return The account info
     * @throws IllegalArgumentException when id or name are null
     */
    public static Account getOrCreateAccount(UUID id, String name) {
        Preconditions.checkNotNull(id, "UUID of player is null?");
        Preconditions.checkNotNull(name, "Name of player " + id + " is null?");

        Account account = getAccount(id);
        if (account == null) {
            account = storeUsername(new PlayerDTO(id, name));
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
        Preconditions.checkNotNull(fullName, "fullName cannot be null!");
        Preconditions.checkArgument(!fullName.isEmpty(), "fullName cannot be empty!");
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
            event.setAccount(getLastAccountFromName(event.getName(), event.searchOfflinePlayers()));
        }
    }

    /**
     * Get account info from a username that might be shortened.
     *
     * @param shortName The name of the player to get the account info
     * @return The account info or <tt>null</tt> if none was found
     * @throws IllegalArgumentException if the username is empty
     * @deprecated Use the {@link AccountQueryEvent} instead!
     */
    @Deprecated
    public static Account getAccountFromShortName(String shortName) {
        Preconditions.checkNotNull(shortName, "shortName cannot be null!");
        Preconditions.checkArgument(!shortName.isEmpty(), "shortName cannot be empty!");
        Account account = null;

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
        return account;
    }

    /**
     * Get the information from the last time a player logged in that previously used the (shortened) name
     *
     * @param name The name of the player to get the last account for
     * @param searchOfflinePlayer Whether or not to search the offline players too
     * @return The last account or <tt>null</tt> if none was found
     * @throws IllegalArgumentException if the username is empty
     */
    private static Account getLastAccountFromName(String name, boolean searchOfflinePlayer) {
        Account account = getAccountFromShortName(name); // first get the account associated with the short name
        if (account == null) {
            account = getAccount(name);
        }
        if (account == null && searchOfflinePlayer && !invalidPlayers.contains(name.toLowerCase(Locale.ROOT))) {
            // no account with that shortname was found, try to get an offline player with that name
            OfflinePlayer offlinePlayer = ChestShop.getBukkitServer().getOfflinePlayer(name);
            if (offlinePlayer != null && offlinePlayer.getName() != null && offlinePlayer.getUniqueId() != null
                    && (!Properties.ENSURE_CORRECT_PLAYERID || offlinePlayer.getUniqueId().version() == uuidVersion)) {
                account = storeUsername(new PlayerDTO(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
            } else {
                invalidPlayers.put(name.toLowerCase(Locale.ROOT), true);
            }
        }
        if (account != null) {
            return getAccount(account.getUuid()); // then get the last account that was online with that UUID
        }
        return null;
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

        Account account = getAccountFromShortName(shortenedName);
        if (account == null) {
            return shortenedName;
        }
        for (int id = 0; account != null; id++) {
            String baseId = Base62.encode(id);
            shortenedName = NameUtil.stripUsername(player.getName(), 15 - 1 - baseId.length()) + ":" + baseId;
            account = getAccountFromShortName(shortenedName);
        }

        return shortenedName;
    }

    public static boolean canUseName(Player player, Permission base, String name) {
        if (ChestShopSign.isAdminShop(name)) {
            return Permission.has(player, Permission.ADMIN_SHOP);
        }

        if (Permission.otherName(player, base, name)) {
            return true;
        }

        AccountQueryEvent queryEvent = new AccountQueryEvent(name);
        ChestShop.callEvent(queryEvent);
        Account account = queryEvent.getAccount();
        if (account == null) {
            return false;
        }
        if (!account.getName().equalsIgnoreCase(name) && Permission.otherName(player, base, account.getName())) {
            return true;
        }
        AccountAccessEvent event = new AccountAccessEvent(player, account);
        ChestShop.callEvent(event);
        return event.canAccess();
    }

    @EventHandler
    public static void onAccountAccessCheck(AccountAccessEvent event) {
        if (!event.canAccess()) {
            event.setAccess(event.getPlayer().getUniqueId().equals(event.getAccount().getUuid()));
        }
    }

    public static boolean isAdminShop(UUID uuid) {
        return adminAccount != null && uuid.equals(adminAccount.getUuid());
    }

    public static boolean isServerEconomyAccount(UUID uuid) {
        return serverEconomyAccount != null && uuid.equals(serverEconomyAccount.getUuid());
    }

    public static void load() {
        if (getUuidVersion() < 0) {
            if (Bukkit.getOnlineMode()) {
                setUuidVersion(4);
            } else if (!Bukkit.getOnlinePlayers().isEmpty()) {
                setUuidVersion(Bukkit.getOnlinePlayers().iterator().next().getUniqueId().version());
            }
        }
        try {
            accounts = DaoCreator.getDaoAndCreateTable(Account.class);

            adminAccount = new Account(Properties.ADMIN_SHOP_NAME, Bukkit.getOfflinePlayer(Properties.ADMIN_SHOP_NAME).getUniqueId());
            accounts.createOrUpdate(adminAccount);

            if (!Properties.SERVER_ECONOMY_ACCOUNT.isEmpty()) {
                serverEconomyAccount = getAccount(Properties.SERVER_ECONOMY_ACCOUNT);
            }
            if (serverEconomyAccount == null && !Properties.SERVER_ECONOMY_ACCOUNT.isEmpty() && !Properties.SERVER_ECONOMY_ACCOUNT_UUID.equals(new UUID(0, 0))) {
                serverEconomyAccount = getOrCreateAccount(Properties.SERVER_ECONOMY_ACCOUNT_UUID, Properties.SERVER_ECONOMY_ACCOUNT);
            }
            if (serverEconomyAccount == null || serverEconomyAccount.getUuid() == null) {
                serverEconomyAccount = null;
                if (!Properties.SERVER_ECONOMY_ACCOUNT.isEmpty()) {
                    ChestShop.getBukkitLogger().log(Level.WARNING, "Server economy account setting '"
                            + Properties.SERVER_ECONOMY_ACCOUNT
                            + "' doesn't seem to be the name of a known player account!" +
                            " Please specify the SERVER_ECONOMY_ACCOUNT_UUID" +
                            " or log in at least once and create a player shop with that account" +
                            " in order for the server economy account to work.");
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
