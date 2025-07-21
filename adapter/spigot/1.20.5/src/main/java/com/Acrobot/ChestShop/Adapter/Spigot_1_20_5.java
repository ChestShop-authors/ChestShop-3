package com.Acrobot.ChestShop.Adapter;

import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.Utils.VersionAdapter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;

public class Spigot_1_20_5 implements Listener, VersionAdapter {


    @EventHandler(priority = EventPriority.HIGH)
    public static void addPotionInfo(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        if (!item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta)) {
            return;
        }

        PotionMeta potionMeta = (PotionMeta) meta;

        StringBuilder message = new StringBuilder();
        if (potionMeta.getBasePotionType() != null) {

            message.append(ChatColor.GRAY);

            message.append(capitalizeFirstLetter(item.getType().name(), '_')).append(" of ");
            message.append(capitalizeFirstLetter(potionMeta.getBasePotionType().getKey().getKey(), '_')).append(' ');

        }

        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            if (message.length() > 0) {
                message.append('\n');
            }
            message.append(ChatColor.DARK_GRAY + capitalizeFirstLetter(effect.getType().getKey().getKey(), '_')
                    + ' ' + (effect.getAmplifier() + 1) + ' ' + toTime(effect.getDuration() / 20));
        }
        if (message.length() > 0) {
            event.addRawMessage("iteminfo_potion", message.toString());
        }
    }

    @Override
    public boolean isSupported() {
        try {
            PotionMeta.class.getMethod("getBasePotionType");
            PotionMeta.class.getMethod("getCustomEffects");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
