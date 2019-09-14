package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInteract;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author g--o
 *
 * Command for toggling chest access
 */
public class AccessToggle implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("csaccess")) {
            String resultMsg = "";
            Player player = (Player) sender;
            String permNode = Permission.OTHER_NAME_ACCESS + ".*";

            if (PlayerInteract.canOpenOtherShops(player)) { // @FIX: why is this deprecated?
                Permission.setPermission(player, permNode, false);
                resultMsg = Messages.TOGGLE_ACCESS_OFF;
            } else {
                Permission.setPermission(player, permNode, true);
                resultMsg = Messages.TOGGLE_ACCESS_ON;
            }

            sender.sendMessage(resultMsg);

            return true;
        }

        return false;
    }
}
