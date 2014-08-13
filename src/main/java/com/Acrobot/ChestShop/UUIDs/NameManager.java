package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.ConnectionManager;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lets you save/cache username and UUID relations
 *
 * @author Andrzej Pomirski (Acrobot)
 */
public class NameManager {
    private static Dao<Account, String> accounts;

    private static BiMap<String, UUID> usernameToUUID = HashBiMap.create();
    private static Map<String, String> shortToLongName = new HashMap<String, String>();

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
            UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();
            usernameToUUID.put(username, uuid);

            return uuid;
        }

        UUID uuid = account.getUuid();

        if (uuid != null) {
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

    public static void storeUsername(final Player player) {
        final UUID uuid = player.getUniqueId();

        if (!usernameToUUID.inverse().containsKey(uuid)) {
            usernameToUUID.inverse().put(uuid, player.getName());
        }

        Bukkit.getScheduler().runTaskAsynchronously(ChestShop.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Account account = null;

                try {
                    account = accounts.queryBuilder().selectColumns("name").where().eq("uuid", uuid).queryForFirst();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

                if (account != null) {
                    return;
                }

                account = new Account(player.getName(), player.getUniqueId());

                try {
                    accounts.createOrUpdate(account);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
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

        return shortenedName.equals(name) || Permission.otherName(player, name);
    }

    public static boolean isAdminShop(UUID uuid) {
        return getUsername(uuid).equals(Properties.ADMIN_SHOP_NAME);
    }

    public static void load() {
        File databaseFile = ChestShop.loadFile("users.db");
        String uri = ConnectionManager.getURI(databaseFile);
        ConnectionSource connection;

        try {
            connection = new JdbcConnectionSource(uri);
            accounts = DaoManager.createDao(connection, Account.class);

            TableUtils.createTableIfNotExists(connection, Account.class);

            Account adminAccount = new Account(Properties.ADMIN_SHOP_NAME, Bukkit.getOfflinePlayer(Properties.ADMIN_SHOP_NAME).getUniqueId());
            accounts.createOrUpdate(adminAccount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
