package com.Acrobot.ChestShop.Database;

import com.Acrobot.ChestShop.ChestShop;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

/**
 * File handling the database migrations
 *
 * @author Andrzej Pomirski
 */
public class Migrations {
    public static final int CURRENT_DATABASE_VERSION = 3;

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
                if (migrateTo2()) {
                    currentVersion++;
                } else {
                    return -1;
                }
            case 2:
                if (migrateTo3()) {
                    currentVersion++;
                } else {
                    return -1;
                }
            case 3:
            default:
                break;
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

    private static boolean migrateTo3() {
        try {
            Dao<Account, String> accountsOld = DaoCreator.getDao(Account.class);
            accountsOld.executeRawNoArgs("ALTER TABLE `accounts` RENAME TO `accounts-old`");

            Dao<Account, String> accounts = DaoCreator.getDaoAndCreateTable(Account.class);

            long start = System.currentTimeMillis();
            try {
                accounts.executeRawNoArgs("INSERT INTO `accounts` (name, shortName, uuid) SELECT name, shortName, uuid FROM `accounts-old`");
            } catch (SQLException e) {
                ChestShop.getBukkitLogger().log(Level.WARNING, "Fast accounts migration failed!\n" + e + "\nCause: " + e.getCause());
                ChestShop.getBukkitLogger().log(Level.INFO, "Starting slow migration...");
                accounts.executeRawNoArgs("DELETE FROM `accounts`");

                GenericRawResults<String[]> results = accounts.queryRaw("SELECT name, shortName, uuid FROM `accounts-old`");
                Date zero = new Date(0);
                int success = 0;
                int error = 0;
                CloseableIterator<String[]> resultIterator = results.closeableIterator();
                long lastInfo = System.currentTimeMillis();
                while (resultIterator.hasNext()) {
                    String[] strings = resultIterator.next();
                    Account account = new Account(strings[0], UUID.fromString(strings[2]));
                    account.setShortName(strings[1]);
                    account.setLastSeen(zero);
                    try {
                        accounts.create(account);
                        success++;
                    } catch (SQLException x) {
                        error++;
                        ChestShop.getBukkitLogger().log(Level.SEVERE, "Could not migrate account " + account.getName() + "/" + account.getShortName() + "/" + account.getUuid() + " to new database format:\n" + x + "\nCause: " + x.getCause());
                        ChestShop.getBukkitLogger().log(Level.INFO, "If the cause is a constraint violation then this is nothing to worry about!");
                    }
                    if (lastInfo + 60 * 1000 < System.currentTimeMillis()) {
                        ChestShop.getBukkitLogger().log(Level.INFO, "Slow migration in progress... " + (System.currentTimeMillis() - start) / 1000.0 + "s elapsed - " + success + " migrated - " + error + " errors");
                        lastInfo = System.currentTimeMillis();
                    }
                }
                results.close();
                ChestShop.getBukkitLogger().log(Level.INFO, success + " accounts successfully migrated. " + error + " accounts failed to migrate!");
            }
            ChestShop.getBukkitLogger().log(Level.INFO, "Migration of accounts table finished in " + (System.currentTimeMillis() - start) / 1000.0 + "s!");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
