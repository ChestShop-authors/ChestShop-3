package com.Acrobot.Breeze.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class MessageUtil {
    public static void sendMessage(CommandSender sender, Language message) {
        String toSend = Config.getLocal(message);

        sender.sendMessage(toSend);
    }

    public static boolean sendMessage(String playerName, Language message) {
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            sendMessage(player, message);
        } else {
            return false;
        }

        return true;
    }
}
