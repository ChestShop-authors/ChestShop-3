package com.Acrobot.ChestShop.Commands;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Permission;
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
        if (args.length < 1) {
            return false;
        }

        Player receiver = (sender instanceof Player ? (Player) sender : null);
        int quantity = 1;

        List<Integer> disregardedIndexes = new ArrayList<Integer>();

        if (args.length > 1) {
            for (int index = args.length - 1; index >= 0; --index) {
                Player target = Bukkit.getPlayer(args[index]);

                if (target == null) {
                    continue;
                }

                receiver = target;
                disregardedIndexes.add(index);
                break;
            }

            for (int index = args.length - 1; index >= 0; --index) {
                if (!NumberUtil.isInteger(args[index]) || Integer.parseInt(args[index]) < 0) {
                    continue;
                }

                quantity = Integer.parseInt(args[index]);
                disregardedIndexes.add(index);

                break;
            }
        }

        if (receiver == null) {
            Messages.PLAYER_NOT_FOUND.sendWithPrefix(sender);
            return true;
        }

        ItemStack item = getItem(args, disregardedIndexes);

        if (MaterialUtil.isEmpty(item)) {
            Messages.INCORRECT_ITEM_ID.sendWithPrefix(sender);
            return true;
        }

        item.setAmount(quantity);
        InventoryUtil.add(item, receiver.getInventory());

        Messages.ITEM_GIVEN.send(sender, "item", MaterialUtil.getName(item), "player", receiver.getName());

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

        ItemParseEvent parseEvent = new ItemParseEvent(builder.toString());
        Bukkit.getPluginManager().callEvent(parseEvent);
        return parseEvent.getItem();
    }
}
