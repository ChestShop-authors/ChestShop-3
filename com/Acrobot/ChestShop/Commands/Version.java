package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.ChestShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Acrobot
 */
public class Version implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChestShop.getPluginName() + "'s version is: " + ChestShop.getVersion());
        return true;
    }
}
