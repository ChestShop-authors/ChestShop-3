package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Acrobot
 */
public class Options implements CommandExecutor {
    public boolean balance;
    public boolean outOfStock;
    public boolean someoneBought;

    public Options() {
        this.balance = true;
        this.outOfStock = true;
        this.someoneBought = true;
    }

    public static boolean exists(String name) {
        name = name.toLowerCase();
        return name.equals("balance") || name.equals("outofstock") || name.equals("someonebought");
    }

    public boolean getOption(String name) {
        name = name.toLowerCase();

        if (name.equals("balance")) {
            return balance;
        }
        if (name.equals("outofstock")) {
            return outOfStock;
        }
        if (name.equals("someonebought")) {
            return someoneBought;
        }
        return false;
    }

    public boolean setOption(String name, boolean value) {
        if (name.equals("balance")) {
            balance = value;
            return true;
        }
        if (name.equals("outofstock")) {
            outOfStock = value;
            return true;
        }
        if (name.equals("someonebought")) {
            someoneBought = value;
            return true;
        }
        return false;
    }

    public static HashMap<Player, Options> playerPreferences = new HashMap<Player, Options>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (!playerPreferences.containsKey(player)) {
            playerPreferences.put(player, new Options());
        }

        if (args.length == 0) {
            String[] options = optionList();

            player.sendMessage(Config.getLocal("options"));

            for (String s : options) {
                player.sendMessage(s);
            }
            return true;
        }

        if (args.length == 1) {
            Options options = playerPreferences.get(player);
            Boolean exists = exists(args[0]);

            if (!exists) {
                return false;
            }

            player.sendMessage(Config.getColored("&a" + args[0] + " is set to: " + options.getOption(args[0])));
            return true;
        }

        if (args.length == 2) {
            try {
                Boolean option = Boolean.parseBoolean(args[1]);
                Options options = playerPreferences.get(player);
                Boolean exists = exists(args[0]);

                if (!exists) {
                    return false;
                }

                Boolean success = options.setOption(args[0], option);
                if (!success) {
                    return false;
                }
                player.sendMessage(Config.getColored("&aSuccessfully set " + args[0] + " to " + args[1]));

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    private static String[] optionList() {
        return new String[]{
                "balance - show current balance after transaction",
                "outOfStock - show that your shop is out of stock",
                "someoneBought - show that someone bought from your shop"
        };
    }
}
