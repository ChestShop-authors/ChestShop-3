package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lets you save/cache username and UUID relations
 *
 * @author Andrzej Pomirski (Acrobot)
 */
@SuppressWarnings("UnusedAssignment") //I deliberately set the variables to null while initializing
public class NameManager {
    private static Dao<Account, String> accounts;

    private static Map<UUID, String> lastSeenName = new HashMap<UUID, String>();
    private static BiMap<String, UUID> usernameToUUID = HashBiMap.create();
    private static Map<String, String> shortToLongName = new HashMap<String, String>();

    public static String getLastSeenName(UUID uuid) {
        if (lastSeenName.containsKey(uuid)) {
            return lastSeenName.get(uuid);
        }

        if (Bukkit.getOfflinePlayer(uuid).getName() != null) {
            String lastSeen = Bukkit.getOfflinePlayer(uuid).getName();

            lastSeenName.put(uuid, lastSeen);
            return lastSeen;
        }

        Account account = null;

        try {
            account = accounts.queryBuilder().selectColumns("lastSeenName", "name").where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            return "";
        }

        if (account.getLastSeenName() != null) {
            lastSeenName.put(uuid, account.getLastSeenName());
        } else if (account.getName() != null) {
            lastSeenName.put(uuid, account.getName());
        }

        return account.getLastSeenName();
    }

    public static UUID getUUID(String username) {
        if (usernameToUUID.containsKey(username)) {
            return usernameToUUID.get(username);
        }

        String shortenedName = NameUtil.stripUsername(username);

        Account account = null;

        try {
            account = accounts.queryBuilder().selectColumns("uuid").where().eq("shortName", shortenedName).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            return Bukkit.getOfflinePlayer(username).getUniqueId();
        }

        UUID uuid = account.getUuid();

        if (uuid != null && !usernameToUUID.containsValue(uuid)) {
            usernameToUUID.put(account.getName(), uuid);
        }

        return uuid;
    }

    public static String getUsername(UUID uuid) {
        if (usernameToUUID.containsValue(uuid)) {
            return usernameToUUID.inverse().get(uuid);
        }

        Account account = null;

        try {
            account = accounts.queryBuilder().selectColumns("name").where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();

            if (name != null) {
                usernameToUUID.put(name, uuid);
                return name;
            }

            return "";
        }

        String name = account.getName();

        if (name != null) {
            usernameToUUID.put(name, uuid);
        }

        return name;
    }

    public static String getFullUsername(String username) {
        if (ChestShopSign.isAdminShop(username)) {
            return Properties.ADMIN_SHOP_NAME;
        }

        String shortName = NameUtil.stripUsername(username);

        if (shortToLongName.containsKey(shortName)) {
            return shortToLongName.get(shortName);
        }

        Account account = null;

        try {
            account = accounts.queryBuilder().selectColumns("name").where().eq("shortName", shortName).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            return username;
        }

        String name = account.getName();

        if (name != null) {
            shortToLongName.put(shortName, name);
        }

        return name;
    }

    public static void storeUsername(final PlayerDTO player) {
        final UUID uuid = player.getUniqueId();

        Account account = null;

        try {
            account = accounts.queryBuilder().where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (account != null) {
            if (account.getName() != null && account.getShortName() == null) {
                String shortenedName = NameUtil.stripUsername(account.getName());

                account.setShortName(shortenedName);
            }

            account.setUuid(uuid); //HOW IS IT EVEN POSSIBLE THAT UUID IS NOT SET EVEN IF WE HAVE FOUND THE PLAYER?!
            account.setLastSeenName(player.getName());

            try {
                accounts.createOrUpdate(account);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return;
        }

        account = new Account(player.getName(), player.getUniqueId());

        if (!usernameToUUID.inverse().containsKey(uuid)) {
            usernameToUUID.inverse().put(uuid, player.getName());
        }

        lastSeenName.put(uuid, player.getName());

        try {
            accounts.createOrUpdate(account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropUsername(final Player player) {
        final UUID uuid = player.getUniqueId();

        if (usernameToUUID.containsValue(uuid)) {
            usernameToUUID.inverse().remove(uuid);
        }

        String shortName = NameUtil.stripUsername(player.getName());

        if (shortToLongName.containsKey(shortName)) {
            shortToLongName.remove(shortName);
        }
    }

    public static boolean canUseName(Player player, String name) {
        String shortenedName = NameUtil.stripUsername(getUsername(player.getUniqueId()));

        if (ChestShopSign.isAdminShop(name)) {
            return false;
        }

        return shortenedName.equals(name) || Permission.otherName(player, name) || player.getUniqueId().equals(getUUID(name));
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
