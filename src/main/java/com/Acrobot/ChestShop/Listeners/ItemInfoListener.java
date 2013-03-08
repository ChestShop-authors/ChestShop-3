package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;

/**
 * @author Acrobot
 */
public class ItemInfoListener implements Listener {
    @EventHandler
    public static void addEnchantment(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        CommandSender sender = event.getSender();

        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            sender.sendMessage(ChatColor.DARK_GRAY + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
        }
    }

    @EventHandler
    public static void addPotionInfo(ItemInfoEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() != Material.POTION || item.getDurability() == 0) {
            return;
        }

        Potion potion;

        try {
            potion = Potion.fromItemStack(item);
        } catch (IllegalArgumentException ex) {
            return;
        }

        StringBuilder message = new StringBuilder(50);

        message.append(ChatColor.GRAY);

        if (potion.getType() == null) {
            return;
        }

        if (potion.isSplash()) {
            message.append("Splash ");
        }

        message.append("Potion of ");
        message.append(capitalizeFirstLetter(potion.getType().name(), '_')).append(' ');
        message.append(toRoman(potion.getLevel()));

        CommandSender sender = event.getSender();

        sender.sendMessage(message.toString());

        for (PotionEffect effect : potion.getEffects()) {
            sender.sendMessage(ChatColor.DARK_GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
        }
    }
}
