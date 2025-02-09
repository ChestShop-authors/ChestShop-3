package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

/**
 * @author KingFaris10
 */
public class Toggle implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            return false;
        }

        Account account = NameManager.getOrCreateAccount(player);
        account.setIgnoreMessages(!account.isIgnoringMessages());

        if (account.isIgnoringMessages()) {
            Messages.TOGGLE_MESSAGES_OFF.sendWithPrefix(player);
        } else {
            Messages.TOGGLE_MESSAGES_ON.sendWithPrefix(player);
        }

        try {
            NameManager.storeAccount(account);
        } catch (Exception e) {
            ChestShop.getBukkitLogger().log(Level.WARNING, "Error while updating account " + account + ":", e);
            Messages.ERROR_OCCURRED.sendWithPrefix(player, "error", "Unable to store account data.");
        }

        return true;
    }

    public static boolean isIgnoring(OfflinePlayer player) {
        return player != null && NameManager.getOrCreateAccount(player).isIgnoringMessages();
    }

    public static boolean isIgnoring(UUID playerId) {
        Account account = NameManager.getAccount(playerId);
        return account != null && account.isIgnoringMessages();
    }

    /**
     * @deprecated Use {@link #isIgnoring(UUID)}
     */
    @Deprecated
    public static boolean isIgnoring(String playerName) {
        return isIgnoring(Bukkit.getOfflinePlayer(playerName));
    }

}
