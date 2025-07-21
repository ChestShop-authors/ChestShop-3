package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ChestShopReloadEvent;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import static com.Acrobot.Breeze.Utils.StringUtil.getMinecraftStringWidth;

/**
 * @author Acrobot
 */
public class ItemAliasModule implements Listener {
    private YamlConfiguration configuration;
    /**
     * Map ChestShop item code -> alias
     */
    private BiMap<String, String> aliases;

    public ItemAliasModule() {
        load();
    }

    private void load() {
        File file = new File(ChestShop.getFolder(), "itemAliases.yml");

        configuration = YamlConfiguration.loadConfiguration(file);

        configuration.options().header(
                "This file specified optional aliases for certain item codes. (Use the full name from /iteminfo)"
                        + "\nPlease note that these aliases should fit on a sign for it to work properly!"
        );

        if (!file.exists()) {
            configuration.addDefault("Item String#3d", "My Cool Item");
            configuration.addDefault("Other Material#Eg", "Some other Item");

            try {
                configuration.options().copyDefaults(true);
                configuration.save(ChestShop.loadFile("itemAliases.yml"));
            } catch (IOException e) {
                ChestShop.getBukkitLogger().log(Level.SEVERE, "Error while saving item aliases config", e);
            }
        }

        aliases = HashBiMap.create(configuration.getKeys(false).size());

        for (String key : configuration.getKeys(false)) {
            if (configuration.isString(key)) {
                aliases.put(key, configuration.getString(key));
            }
        }
    }

    @EventHandler
    public void onReload(ChestShopReloadEvent event) {
        load();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemParse(ItemParseEvent event) {
        String code = aliases.inverse().get(event.getItemString());
        if (code == null) {
            String[] typeParts = event.getItemString().replaceAll("(?<!^)([A-Z1-9])", "_$1").toUpperCase(Locale.ROOT).split("[ _\\-]");
            int length = Short.MAX_VALUE;
            for (Map.Entry<String, String> entry : aliases.entrySet()) {
                if (entry.getValue().length() < length && entry.getValue().toUpperCase(Locale.ROOT).startsWith(event.getItemString().toUpperCase(Locale.ROOT))) {
                    length = (short) entry.getValue().length();
                    code = entry.getKey();
                } else if (typeParts.length > 1) {
                    String[] nameParts = entry.getValue().toUpperCase(Locale.ROOT).split("[ _\\-]");
                    if (typeParts.length == nameParts.length) {
                        boolean matched = true;
                        for (int i = 0; i < nameParts.length; i++) {
                            if (!nameParts[i].startsWith(typeParts[i])) {
                                matched = false;
                                break;
                            }
                        }
                        if (matched) {
                            code = entry.getKey();
                            break;
                        }
                    }
                }
            }
        }
        if (code != null) {
            event.setItem(MaterialUtil.getItem(code));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemStringQuery(ItemStringQueryEvent event) {
        if (event.getItemString() != null) {
            String newCode = null;

            if (aliases.containsKey(event.getItemString())) {
                newCode = aliases.get(event.getItemString());
            } else if (!event.getItemString().contains("#")) {
                newCode = aliases.get(event.getItemString().toLowerCase(Locale.ROOT));
            } else {
                String[] parts = event.getItemString().split("#", 2);
                String lowercaseCode = parts[0].toLowerCase(Locale.ROOT) + "#" + parts[1];
                if (aliases.containsKey(lowercaseCode)) {
                    newCode = aliases.get(lowercaseCode);
                }
            }

            if (newCode != null) {
                if (event.getMaxWidth() > 0) {
                    int width = getMinecraftStringWidth(newCode);
                    if (width > event.getMaxWidth()) {
                        ChestShop.getBukkitLogger().warning("Can't use configured alias " + newCode + " as it's width (" + width + ") was wider than the allowed max width of " + event.getMaxWidth());
                        return;
                    }
                }
                event.setItemString(newCode);
            }
        }
    }
}
