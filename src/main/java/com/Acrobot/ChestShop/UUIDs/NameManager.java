package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.ConnectionManager;
import com.Acrobot.ChestShop.Permission;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Lets you save/cache username and UUID relations
 *
 * @author Andrzej Pomirski (Acrobot)
 */
public class NameManager {
    private static Dao<Account, String> accounts;

    public static UUID getUUID(String username) {
        String shortenedName = NameUtil.stripUsername(username);

        Account account = null;

        try {
            account = accounts.queryBuilder().selectColumns("uuid").where().eq("shortName", shortenedName).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            return null;
        }

        return account.getUuid();
    }

    public static String getUsername(UUID uuid) {
        Account account = null;

        try {
            account = accounts.queryBuilder().selectColumns("name").where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            return "";
        }

        return account.getName();
    }

    public static String getFullUsername(String username) {
        String shortName = NameUtil.stripUsername(username);
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

        return account.getName();
    }

    public static void storeUsername(Player player) {
        UUID uuid = player.getUniqueId();

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
            accounts.create(account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean canUseName(Player player, String name) {
        String shortenedName = NameUtil.stripUsername(getUsername(player.getUniqueId()));

        return shortenedName.equals(name) || Permission.otherName(player, name);
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
            accounts.createIfNotExists(adminAccount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
