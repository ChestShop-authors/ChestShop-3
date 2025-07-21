package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ChestShopReloadEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Acrobot
 */
public class Version implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && args[0].equals("reload")) {
            ChestShop.callEvent(new ChestShopReloadEvent(sender));

            sender.sendMessage(ChatColor.DARK_GREEN + "The config was reloaded.");
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + ChestShop.getPluginName() + "'s version is: " + ChatColor.GREEN + ChestShop.getVersion());
        return true;
    }
}
