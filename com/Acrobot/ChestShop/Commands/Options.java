package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Options.Option;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Acrobot
 */
public class Options implements CommandExecutor {

    public static boolean exists(String name) {
        name = name.toLowerCase();
        return Option.getOption(name) != null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
        /*if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (!playerpref.containsKey(player)) {
            setDefault(player);
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
            Boolean exists = exists(args[0]);

            if (!exists) {
                return false;
            }

            player.sendMessage(Config.getColored("&a" + args[0] + " is set to: " + playerpref.get(player).getOption(args[0])));
            return true;
        }

        if (args.length == 2) {
            try {
                Boolean option = Boolean.parseBoolean(args[1]);
                Options options = playerpref.get(player);
                Boolean exists = exists(args[0]);

                if (!exists) {
                    return false;
                }

                Boolean success = options.setOption(args[0], option);
                if (!success) {
                    return false;
                }
                playerpref.put(player, Option.va)
                player.sendMessage(Config.getColored("&aSuccessfully set " + args[0] + " to " + args[1]));

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;*/
    }

    private static String[] optionList() {
        return new String[]{
                "balance - show current balance after transaction",
                "outOfStock - show that your shop is out of stock",
                "someoneBought - show that someone bought from your shop"
        };
    }
}
