package com.Acrobot.ChestShop.Commands;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo;

/**
 * @author Acrobot
 */
public class ItemInfo implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ItemStack item;

        if (args.length == 0) {
            if (!(sender instanceof HumanEntity)) {
                return false;
            }

            item = ((HumanEntity) sender).getItemInHand();
        } else {
            ItemParseEvent parseEvent = new ItemParseEvent(StringUtil.joinArray(args));
            Bukkit.getPluginManager().callEvent(parseEvent);
            item = parseEvent.getItem();
        }

        if (MaterialUtil.isEmpty(item)) {
            return false;
        }

        sender.sendMessage(Messages.prefix(iteminfo));
        try {
            sender.sendMessage(ChatColor.WHITE + "Full Name: " + ChatColor.GRAY + MaterialUtil.getName(item));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Error while generating full name. Please contact an admin or take a look at the console/log!");
            ChestShop.getPlugin().getLogger().log(Level.SEVERE, "Error while generating full item name", e);
            return true;
        }

        try {
            sender.sendMessage(ChatColor.WHITE + "Shop Sign: " + ChatColor.GRAY + MaterialUtil.getSignName(item));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Error while generating shop sign name. Please contact an admin or take a look at the console/log!");
            ChestShop.getPlugin().getLogger().log(Level.SEVERE, "Error while generating shop sign item name", e);
            return true;
        }

        ItemInfoEvent event = new ItemInfoEvent(sender, item);
        ChestShop.callEvent(event);

        return true;
    }

    private static String getNameAndID(ItemStack item) {
        return MaterialUtil.getName(item);
    }

    private static String getDurability(ItemStack item) {
        if (item.getDurability() != 0) {
            return ChatColor.DARK_GREEN + ":" + Integer.toString(item.getDurability());
        } else {
            return "";
        }
    }

    private static String getMetadata(ItemStack item) {
        if (!item.hasItemMeta()) {
            return "";
        }

        return ChatColor.GOLD + "#" + MaterialUtil.Metadata.getItemCode(item);
    }
}
