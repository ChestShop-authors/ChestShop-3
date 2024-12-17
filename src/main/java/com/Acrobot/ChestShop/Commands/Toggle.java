// Toggle.java
package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.Dao;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Command executor class for toggling the ignoring state of players.
 */
public class Toggle implements CommandExecutor {
    private static Dao<Account, UUID> accountDao;

    // Static block to initialize the DAO for Account
    static {
        try {
            accountDao = DaoCreator.getDaoAndCreateTable(Account.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the command execution to toggle the ignoring state.
     *
     * @param sender The command sender.
     * @param command The command.
     * @param label The command label.
     * @param args The command arguments.
     * @return true if the command was successful, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            return false;
        }

        try {
            if (setIgnoring(player, !isIgnoring(player))) {
                Messages.TOGGLE_MESSAGES_OFF.sendWithPrefix(player);
            } else {
                Messages.TOGGLE_MESSAGES_ON.sendWithPrefix(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Checks if the player is in the ignoring state.
     *
     * @param player The player to check.
     * @return true if the player is ignoring, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static boolean isIgnoring(OfflinePlayer player) throws SQLException {
        return player != null && isIgnoring(player.getUniqueId());
    }

    /**
     * Checks if the player with the given UUID is in the ignoring state.
     *
     * @param playerId The UUID of the player to check.
     * @return true if the player is ignoring, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static boolean isIgnoring(UUID playerId) throws SQLException {
        Account account = accountDao.queryForId(playerId);
        return account != null && account.isToggled();
    }

    /**
     * Sets the ignoring state for the player.
     *
     * @param player The player to set the state for.
     * @param ignoring The new ignoring state.
     * @return true if the state was successfully set, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static boolean setIgnoring(Player player, boolean ignoring) throws SQLException {
        Preconditions.checkNotNull(player);

        UUID playerId = player.getUniqueId();
        Account account = accountDao.queryForId(playerId);

        if (account == null) {
            account = new Account(player.getName(), playerId);
            account.setToggled(ignoring);
            accountDao.create(account);
        } else {
            account.setToggled(ignoring);
            accountDao.update(account);
        }

        return ignoring;
    }
}