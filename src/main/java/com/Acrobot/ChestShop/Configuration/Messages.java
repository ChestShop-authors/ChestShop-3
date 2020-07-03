package com.Acrobot.ChestShop.Configuration;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.ChestShop;
import de.themoep.minedown.MineDown;
import de.themoep.utils.lang.bukkit.BukkitLanguageConfig;
import de.themoep.utils.lang.bukkit.LanguageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Acrobot
 */
public class Messages {
    public static Message prefix;
    public static Message iteminfo;
    public static Message iteminfo_fullname;
    public static Message iteminfo_shopname;
    public static Message iteminfo_repaircost;
    public static Message iteminfo_book;
    public static Message iteminfo_book_generation;
    public static Message iteminfo_lore;

    public static Message METRICS;

    public static Message ACCESS_DENIED;
    public static Message TRADE_DENIED;

    public static Message NOT_ENOUGH_MONEY;
    public static Message NOT_ENOUGH_MONEY_SHOP;

    public static Message CLIENT_DEPOSIT_FAILED;
    public static Message SHOP_DEPOSIT_FAILED;
    public static Message NO_ECONOMY_ACCOUNT;

    public static Message NO_BUYING_HERE;
    public static Message NO_SELLING_HERE;

    public static Message NOT_ENOUGH_SPACE_IN_INVENTORY;
    public static Message NOT_ENOUGH_SPACE_IN_CHEST;
    public static Message NOT_ENOUGH_ITEMS_TO_SELL;
    public static Message NOT_ENOUGH_SPACE_IN_YOUR_SHOP;

    public static Message NOT_ENOUGH_STOCK;
    public static Message NOT_ENOUGH_STOCK_IN_YOUR_SHOP;

    public static Message YOU_BOUGHT_FROM_SHOP;
    public static Message SOMEBODY_BOUGHT_FROM_YOUR_SHOP;

    public static Message YOU_SOLD_TO_SHOP;
    public static Message SOMEBODY_SOLD_TO_YOUR_SHOP;

    public static Message YOU_CANNOT_CREATE_SHOP;
    public static Message NO_CHEST_DETECTED;
    public static Message INVALID_SHOP_DETECTED;
    public static Message INVALID_SHOP_PRICE;
    public static Message INVALID_SHOP_QUANTITY;
    public static Message CANNOT_ACCESS_THE_CHEST;

    public static Message SELL_PRICE_ABOVE_MAX;
    public static Message SELL_PRICE_BELOW_MIN;
    public static Message BUY_PRICE_ABOVE_MAX;
    public static Message BUY_PRICE_BELOW_MIN;

    public static Message CLICK_TO_AUTOFILL_ITEM;
    public static Message NO_ITEM_IN_HAND;

    public static Message PROTECTED_SHOP;
    public static Message PROTECTED_SHOP_SIGN;
    public static Message SHOP_CREATED;
    public static Message SHOP_FEE_PAID;
    public static Message SHOP_REFUNDED;
    public static Message ITEM_GIVEN;

    public static Message RESTRICTED_SIGN_CREATED;

    public static Message PLAYER_NOT_FOUND;
    public static Message NO_PERMISSION;
    public static Message INCORRECT_ITEM_ID;
    public static Message NOT_ENOUGH_PROTECTIONS;

    public static Message CANNOT_CREATE_SHOP_HERE;

    public static Message TOGGLE_MESSAGES_OFF;
    public static Message TOGGLE_MESSAGES_ON;

    public static Message TOGGLE_ACCESS_ON;
    public static Message TOGGLE_ACCESS_OFF;

    @Deprecated
    public static String prefix(String message) {
        return Configuration.getColoured(prefix.getLang(null) + message);
    }

    @Deprecated
    public static String replace(String message, String... replacements) {
        for (int i = 0; i + 1 < replacements.length; i+=2) {
            message = message.replace("%" + replacements[i], replacements[i+1]);
        }
        return Configuration.getColoured(message);
    }

    private static LanguageManager manager;

    public static void load() {
        for (Field field : Messages.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                field.set(null, new Message(field.getName()));
            } catch (IllegalAccessException e) {
                ChestShop.getBukkitLogger().log(Level.SEVERE, "Error while setting Message " + field.getName() + "!", e);
            }
        }
        manager = new LanguageManager(ChestShop.getPlugin(), Properties.DEFAULT_LANGUAGE);

        // Legacy locale.yml file
        File legacyFile = new File(ChestShop.getPlugin().getDataFolder(), "local.yml");
        if (legacyFile.exists()) {
            ChestShop.getBukkitLogger().log(Level.INFO, "Found legacy local.yml. Loading it as 'legacy' language and using that for all messages.");
            ChestShop.getBukkitLogger().log(Level.INFO, "As long as the legacy file is used automatic language switching based on the client settings will not be supported!");
            ChestShop.getBukkitLogger().log(Level.INFO, "Import it into the corresponding language file and remove/rename the file if you don't want it anymore!");
            manager.addConfig(new BukkitLanguageConfig(ChestShop.getPlugin(), "", legacyFile, "legacy", false));
            manager.setDefaultLocale("legacy");
            Properties.USE_CLIENT_LOCALE = false;
        }

        if (!Properties.USE_CLIENT_LOCALE) {
            manager.setProvider(sender -> null);
        }
    }

    public static class Message {
        private String key;

        public Message(String key) {
            this.key = key;
        }

        public void sendWithPrefix(CommandSender sender, Map<String, String> replacementMap, String... replacements) {
            sender.spigot().sendMessage(getComponents(sender, true, replacementMap, replacements));
        }

        public void sendWithPrefix(CommandSender sender, Map<String, String> replacements) {
            sender.spigot().sendMessage(getComponents(sender, true, replacements));
        }

        public void sendWithPrefix(CommandSender sender, String... replacements) {
            sender.spigot().sendMessage(getComponents(sender, true, Collections.emptyMap(), replacements));
        }

        public void send(CommandSender sender, String... replacements) {
            sender.spigot().sendMessage(getComponents(sender, false, Collections.emptyMap(), replacements));
        }

        public BaseComponent[] getComponents(CommandSender sender, boolean prefixSuffix, Map<String, String> replacementMap, String... replacements) {
            MineDown mineDown = new MineDown("%prefix" + getLang(sender));
            mineDown.placeholderSuffix("");
            mineDown.replace("prefix", prefixSuffix ? prefix.getLang(sender) : "");
            mineDown.replace(replacementMap);
            mineDown.replace(replacements);
            return mineDown.toComponent();
        }

        private String getLang(CommandSender sender) {
            return manager.getConfig(sender).get(key);
        }

        public String getTextWithPrefix(CommandSender sender, Map<String, String> replacementMap, String... replacements) {
            return TextComponent.toLegacyText(getComponents(sender, true, replacementMap, replacements));
        }

        public String getTextWithPrefix(CommandSender sender, String... replacements) {
            return getTextWithPrefix(sender, Collections.emptyMap(), replacements);
        }

        public String getTextWithPrefix(CommandSender sender, Map<String, String> replacements) {
            return getTextWithPrefix(sender, replacements, new String[0]);
        }

        public String getKey() {
            return key;
        }
    }
}
