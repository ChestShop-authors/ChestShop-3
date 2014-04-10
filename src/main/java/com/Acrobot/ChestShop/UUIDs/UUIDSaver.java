package com.Acrobot.ChestShop.UUIDs;

import com.Acrobot.Breeze.Utils.NameUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.ConnectionManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.Acrobot.Breeze.Utils.NameUtil.*;

/**
 * Lets you save/cache username and UUID relations
 * @author Andrzej Pomirski (Acrobot)
 */
public class UUIDSaver {
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

    public static void load() {
        File databaseFile = ChestShop.loadFile("users.db");
        String uri = ConnectionManager.getURI(databaseFile);
        ConnectionSource connection;

        try {
            connection = new JdbcConnectionSource(uri);
            accounts = DaoManager.createDao(connection, Account.class);

            TableUtils.createTable(connection, Account.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
