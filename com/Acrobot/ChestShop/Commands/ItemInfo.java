package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Utils.Config;
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
        boolean isPlayer = (sender instanceof Player);

        if (args.length == 0) {
            if (!isPlayer) {
                return false;
            }
            Player player = (Player) sender;

            ItemStack itemInHand = player.getItemInHand();

            if (itemInHand.getType() == Material.AIR) {
                return false;
            }

            player.sendMessage(Config.getLocal("iteminfo"));
            player.sendMessage(itemInHand.getTypeId() + ":" + itemInHand.getDurability() + " - " + itemInHand.getType().name());

            return true;
        } else {
            ItemStack item = Items.getItemStack(args[0]);

            if (item == null) {
                return false;
            }

            sender.sendMessage(Config.getLocal("iteminfo"));
            sender.sendMessage(item.getTypeId() + ":" + item.getDurability() + " - " + item.getType().name());

            return true;
        }
    }
}
