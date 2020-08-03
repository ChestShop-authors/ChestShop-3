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
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_fullname;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_shopname;
import static com.Acrobot.ChestShop.Configuration.Messages.replace;

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
    
        ItemInfoEvent event = new ItemInfoEvent(sender, item);
        ChestShop.callEvent(event);
    
        return !event.isCancelled() && !MaterialUtil.isEmpty(item);
    }
}
