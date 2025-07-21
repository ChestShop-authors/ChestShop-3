package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Listeners.Modules.MetricsModule;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Acrobot
 */
public class Metrics implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Messages.METRICS.send(sender,
                "accounts", String.valueOf(NameManager.getAccountCount()),
                "totalTransactions", String.valueOf(MetricsModule.getTotalTransactions()),
                "buyTransactions", String.valueOf(MetricsModule.getBuyTransactions()),
                "sellTransactions", String.valueOf(MetricsModule.getSellTransactions()),
                "totalItems", String.valueOf(MetricsModule.getTotalItemsCount()),
                "boughtItems", String.valueOf(MetricsModule.getBoughtItemsCount()),
                "soldItems", String.valueOf(MetricsModule.getSoldItemsCount())
        );
        return true;
    }
}
