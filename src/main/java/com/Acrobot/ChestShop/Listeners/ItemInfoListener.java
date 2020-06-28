package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_book;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_book_generation;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_lore;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_repaircost;

/**
 * @author Acrobot
 */
public class ItemInfoListener implements Listener {

    @EventHandler
    public static void addRepairCost(ItemInfoEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta instanceof Repairable && ((Repairable) meta).getRepairCost() > 0) {
            iteminfo_repaircost.send(event.getSender(), "cost", String.valueOf(((Repairable) meta).getRepairCost()));
        }
    }

    @EventHandler
    public static void addEnchantment(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        CommandSender sender = event.getSender();

        for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
            sender.sendMessage(ChatColor.AQUA + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
        }

        if (meta instanceof EnchantmentStorageMeta) {
            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                sender.sendMessage(ChatColor.YELLOW + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
            }
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

    @EventHandler
    public static void addBookInfo(ItemInfoEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta instanceof BookMeta) {
            BookMeta book = (BookMeta) meta;
            iteminfo_book.send(event.getSender(),
                    "title", book.getTitle(),
                    "author", book.getAuthor(),
                    "pages", String.valueOf(book.getPageCount())
            );
            if (book.hasGeneration()) {
                iteminfo_book_generation.send(event.getSender(),
                        "generation", StringUtil.capitalizeFirstLetter(book.getGeneration().name(), '_')
                );
            }
        }
    }

    @EventHandler
    public static void addLoreInfo(ItemInfoEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta.hasLore()) {
            iteminfo_lore.send(event.getSender(), "lore", String.join("\n", meta.getLore()));
        }
    }
}
