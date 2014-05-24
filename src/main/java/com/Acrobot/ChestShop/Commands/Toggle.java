package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Permission;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KingFaris10
 */
public class Toggle implements CommandExecutor {
    private static final List<String> toggledPlayers = new ArrayList<String>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (!Permission.has(sender, Permission.NOTIFY_TOGGLE)) {
            sender.sendMessage(Messages.ACCESS_DENIED);
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            return false;
        }

        if (setIgnoring(player, !toggledPlayers.contains(player.getName()))) {
            player.sendMessage(Messages.prefix(Messages.TOGGLE_MESSAGES_OFF));
        } else {
            player.sendMessage(Messages.prefix(Messages.TOGGLE_MESSAGES_ON));
        }

        return true;
    }

    public static void clearToggledPlayers() {
        toggledPlayers.clear();
    }

    public static boolean isIgnoring(OfflinePlayer player) {
        return player != null && toggledPlayers.contains(player.getName());
    }

    public static boolean setIgnoring(Player player, boolean ignoring) {
        Validate.notNull(player); // Make sure the player instance is not null, in case there are any errors in the code

        if (ignoring) {
            if (!toggledPlayers.contains(player.getName())) {
                toggledPlayers.add(player.getName());
            }
        } else {
            if (toggledPlayers.contains(player.getName())) {
                toggledPlayers.remove(player.getName());
            }
        }

        return ignoring;
    }

}
