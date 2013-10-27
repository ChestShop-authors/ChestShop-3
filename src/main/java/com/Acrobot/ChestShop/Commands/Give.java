package com.Acrobot.ChestShop.Commands;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Acrobot
 */
public class Give implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player receiver = (sender instanceof Player ? (Player) sender : null);
        int quantity = 1;

        List<Integer> disregardedIndexes = new ArrayList<Integer>();

        if (args.length > 1) {
            for (int index = args.length - 1; index > 0; ++index) {
                Player target = Bukkit.getPlayer(args[index]);

                if (target == null) {
                    continue;
                }

                receiver = target;
                disregardedIndexes.add(index);
                break;
            }

            for (int index = args.length - 1; index > 0; ++index) {
                if (!NumberUtil.isInteger(args[index]) && Integer.parseInt(args[index]) > 0) {
                    continue;
                }

                quantity = Integer.parseInt(args[index]);
                disregardedIndexes.add(index);

                break;
            }
        }

        if (receiver == null) {
            sender.sendMessage(Messages.prefix(Messages.PLAYER_NOT_FOUND));
            return true;
        }

        ItemStack item = getItem(args, disregardedIndexes);

        if (item == null) {
            sender.sendMessage(Messages.prefix(Messages.INCORRECT_ITEM_ID));
            return true;
        }

        item.setAmount(quantity);
        InventoryUtil.add(item, receiver.getInventory());

        sender.sendMessage(Messages.prefix(Messages.ITEM_GIVEN
                .replace("%item", MaterialUtil.getName(item))
                .replace("player", receiver.getName())));

        return true;
    }

    private static ItemStack getItem(String[] arguments, List<Integer> disregardedElements) {
        StringBuilder builder = new StringBuilder(arguments.length * 5);

        for (int index = 0; index < arguments.length; ++index) {
            if (disregardedElements.contains(index)) {
                continue;
            }

            builder.append(arguments[index]).append(' ');
        }

        return MaterialUtil.getItem(builder.toString());
    }
}
