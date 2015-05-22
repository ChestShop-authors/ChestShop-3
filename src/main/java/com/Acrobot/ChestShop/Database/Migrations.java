package com.Acrobot.ChestShop.Database;

import com.Acrobot.ChestShop.ChestShop;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * File handling the database migrations
 *
 * @author Andrzej Pomirski
 */
public class Migrations {
    public static final int CURRENT_DATABASE_VERSION = 2;

    /**
     * Migrates a database from the given version
     *
     * @param currentVersion Current version of the database
     * @return Current database version
     */
    public static int migrate(int currentVersion) {
        if (currentVersion != CURRENT_DATABASE_VERSION) {
            ChestShop.getBukkitLogger().info("Updating database...");
        } else {
            return CURRENT_DATABASE_VERSION;
        }

        switch (currentVersion) {
            case 1:
                boolean migrated = migrateTo2();

                if (migrated) {
                    currentVersion++;
                }

            case 2:
            default:
                //do nothing
        }

        return currentVersion;
    }

    private static boolean migrateTo2() {
        try {
            Dao<Account, String> accounts = DaoCreator.getDao(Account.class);

            accounts.executeRaw("ALTER TABLE `accounts` ADD COLUMN lastSeenName VARCHAR");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
