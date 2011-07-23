package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Items.Items;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class ItemInfo implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;

            ItemStack itemInHand = player.getItemInHand();

            if (itemInHand.getType() == Material.AIR) return false;

            player.sendMessage(Config.getLocal(Language.iteminfo));
            player.sendMessage(itemInHand.getTypeId() + ":" + itemInHand.getDurability() + " - " + itemInHand.getType().name());

            return true;
        } else {
            ItemStack item = Items.getItemStack(args[0]);

            if (item == null) return false;

            sender.sendMessage(Config.getLocal(Language.iteminfo));
            sender.sendMessage(item.getTypeId() + ":" + item.getDurability() + " - " + item.getType().name());

            return true;
        }
    }
}
