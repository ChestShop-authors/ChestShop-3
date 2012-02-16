package com.Acrobot.ChestShop.Commands;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Language;
import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Utils.uEnchantment;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Acrobot
 */
public class ItemInfo implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ItemStack item;
        if (args.length == 0) {
            if (!(sender instanceof Player)) return false;
            item = ((Player) sender).getItemInHand();
        } else {
            item = Items.getItemStack(joinArray(args));
        }

        if (item == null || item.getType() == Material.AIR) return false;
        
        String durability = (item.getDurability() != 0 ? ChatColor.DARK_GREEN + ":" + item.getDurability() : "");
        String ench = uEnchantment.getEnchantment(item);
        String enchantment = (ench != null ? ChatColor.DARK_AQUA + "-" + ench : "");

        sender.sendMessage(Config.getLocal(Language.iteminfo));
        String itemname = Items.getName(item);
        sender.sendMessage(ChatColor.GRAY + itemname + ChatColor.WHITE + "      "
                + item.getTypeId() + durability + enchantment + ChatColor.WHITE);

        Map<Enchantment, Integer> map = item.getEnchantments();
        for (Map.Entry<Enchantment, Integer> e : map.entrySet())
            sender.sendMessage(ChatColor.DARK_GRAY + uSign.capitalizeFirst(e.getKey().getName()) + ' ' + intToRoman(e.getValue()));

        return true;

    }
    
    private static String intToRoman(int integer){
        if (integer == 1) return "I";
        if (integer == 2) return "II";
        if (integer == 3) return "III";
        if (integer == 4) return "IV";
        if (integer == 5) return "V";
        return Integer.toString(integer);
    }
    

    
    private static String joinArray(String[] array){
        StringBuilder b = new StringBuilder(array.length);
        for (String s : array) b.append(s).append(' ');
        return b.toString();
    }
}
