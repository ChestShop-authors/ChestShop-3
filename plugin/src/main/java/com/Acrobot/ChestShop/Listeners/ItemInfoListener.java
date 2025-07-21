package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_book;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_book_generation;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_leather_color;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_lore;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_map_location;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_map_view;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_recipes;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_repaircost;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_tropical_fish;

/**
 * @author Acrobot
 */
public class ItemInfoListener implements Listener {

    @EventHandler
    public static void addRepairCost(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof Repairable && ((Repairable) meta).getRepairCost() > 0) {
                event.addMessage(iteminfo_repaircost, "cost", String.valueOf(((Repairable) meta).getRepairCost()));
            }
        }
    }

    @EventHandler
    public static void addEnchantment(ItemInfoEvent event) {
        ItemStack item = event.getItem();

        List<String> lines = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
            lines.add(ChatColor.AQUA + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
        }

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                    lines.add(ChatColor.YELLOW + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
                }
            }
        }

        event.addRawMessage("iteminfo_enchantments", String.join("\n", lines));
    }

    @EventHandler
    public static void addLeatherColor(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof LeatherArmorMeta) {
                Color color = ((LeatherArmorMeta) meta).getColor();
                event.addMessage(iteminfo_leather_color,
                        "colorred", String.valueOf(color.getRed()),
                        "colorgreen", String.valueOf(color.getGreen()),
                        "colorblue", String.valueOf(color.getBlue()),
                        "colorhex", Integer.toHexString(color.asRGB())
                );
            }
        }
    }

    @EventHandler
    public static void addRecipes(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof KnowledgeBookMeta && !((KnowledgeBookMeta) meta).getRecipes().isEmpty()) {
                event.addMessage(iteminfo_recipes);
                for (NamespacedKey recipe : ((KnowledgeBookMeta) meta).getRecipes()) {
                    event.getSender().sendMessage(ChatColor.GRAY + recipe.toString());
                }
            }
        }
    }

    @EventHandler
    public static void addTropicalFishInfo(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof TropicalFishBucketMeta && ((TropicalFishBucketMeta) meta).hasVariant()) {
                event.addMessage(iteminfo_tropical_fish,
                        "pattern", capitalizeFirstLetter(((TropicalFishBucketMeta) meta).getPattern().name()),
                        "patterncolor", capitalizeFirstLetter(((TropicalFishBucketMeta) meta).getPatternColor().name()),
                        "bodycolor", capitalizeFirstLetter(((TropicalFishBucketMeta) meta).getBodyColor().name())
                );
            }
        }
    }

    @EventHandler
    public static void addMapInfo(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof MapMeta) {
                if (((MapMeta) meta).getMapView() != null) {
                    MapView mapView = ((MapMeta) meta).getMapView();
                    event.addMessage(iteminfo_map_view,
                            "id", String.valueOf(mapView.getId()),
                            "x", String.valueOf(mapView.getCenterX()),
                            "z", String.valueOf(mapView.getCenterZ()),
                            "world", mapView.getWorld() != null ? mapView.getWorld().getName() : "unknown",
                            "scale", capitalizeFirstLetter(mapView.getScale().name(), '_'),
                            "locked", "false"
                    );
                }
                if (((MapMeta) meta).hasLocationName()) {
                    event.addMessage(iteminfo_map_location, "location", String.valueOf(((MapMeta) meta).getLocationName()));
                }
            }
        }
    }

    @EventHandler
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

        message.append(ChatColor.GRAY);

        message.append(capitalizeFirstLetter(item.getType().name(), '_')).append(" of ");
        message.append(capitalizeFirstLetter(potionMeta.getBasePotionData().getType().name(), '_')).append(' ');
        if (potionMeta.getBasePotionData().isUpgraded()) {
            message.append("II");
        } else if (potionMeta.getBasePotionData().isExtended()) {
            message.append("+");
        }

        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            message.append("\n" + ChatColor.DARK_GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
        }
        event.addRawMessage("iteminfo_potion", message.toString());
    }

    @EventHandler
    public static void addBookInfo(ItemInfoEvent event) {
        if (!event.getItem().hasItemMeta()) {
            return;
        }
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta instanceof BookMeta) {
            BookMeta book = (BookMeta) meta;
            event.addMessage(iteminfo_book,
                    "title", book.getTitle(),
                    "author", book.getAuthor(),
                    "pages", String.valueOf(book.getPageCount())
            );
            if (book.hasGeneration()) {
                event.addMessage(iteminfo_book_generation,
                        "generation", StringUtil.capitalizeFirstLetter(book.getGeneration().name(), '_')
                );
            }
        }
    }

    @EventHandler
    public static void addLoreInfo(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta.hasLore()) {
                event.addMessage(iteminfo_lore, "lore", String.join("\n", meta.getLore()));
            }
        }
    }
}
