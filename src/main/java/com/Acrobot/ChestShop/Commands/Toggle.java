package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Configuration.Messages;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author KingFaris10
 */
public class Toggle implements CommandExecutor {
    private static final Set<UUID> toggledPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            return false;
        }

        if (setIgnoring(player, !isIgnoring(player))) {
            Messages.TOGGLE_MESSAGES_OFF.sendWithPrefix(player);
        } else {
            Messages.TOGGLE_MESSAGES_ON.sendWithPrefix(player);
        }

        return true;
    }

    public static void clearToggledPlayers() {
        toggledPlayers.clear();
    }

    public static boolean isIgnoring(OfflinePlayer player) {
        return player != null && isIgnoring(player.getUniqueId());
    }

    public static boolean isIgnoring(UUID playerId) {
        return toggledPlayers.contains(playerId);
    }

    /**
     * @deprecated Use {@link #isIgnoring(UUID)}
     */
    @Deprecated
    public static boolean isIgnoring(String playerName) {
        return isIgnoring(Bukkit.getOfflinePlayer(playerName));
    }

    public static boolean setIgnoring(Player player, boolean ignoring) {
        Validate.notNull(player); // Make sure the player instance is not null, in case there are any errors in the code

        if (ignoring) {
            toggledPlayers.add(player.getUniqueId());
        } else {
            toggledPlayers.remove(player.getUniqueId());
        }

        return ignoring;
    }

}
