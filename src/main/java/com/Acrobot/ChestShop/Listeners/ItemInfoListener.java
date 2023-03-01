package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
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

import java.util.Map;

import static com.Acrobot.Breeze.Utils.NumberUtil.toRoman;
import static com.Acrobot.Breeze.Utils.NumberUtil.toTime;
import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_axolotl_variant;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_book;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_book_generation;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_bundle_items;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_crossbow_projectile;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_crossbow_projectiles;
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

    // Register version dependent listeners
    static {
        try {
            Class.forName("org.bukkit.inventory.meta.AxolotlBucketMeta");
            ChestShop.registerListener(new Listener() {
                @EventHandler
                public void addAxolotlInfo(ItemInfoEvent event) {
                    if (event.getItem().hasItemMeta()) {
                        ItemMeta meta = event.getItem().getItemMeta();
                        if (meta instanceof AxolotlBucketMeta) {
                            iteminfo_axolotl_variant.send(event.getSender(), "variant", capitalizeFirstLetter(((AxolotlBucketMeta) meta).getVariant().name(), '_'));
                        }
                    }
                }
            });
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
        try {
            Class.forName("org.bukkit.inventory.meta.BundleMeta");
            ChestShop.registerListener(new Listener() {
                @EventHandler
                public void addAxolotlInfo(ItemInfoEvent event) {
                    if (event.getItem().hasItemMeta()) {
                        ItemMeta meta = event.getItem().getItemMeta();
                        if (meta instanceof BundleMeta) {
                            iteminfo_bundle_items.send(event.getSender(), "itemcount", String.valueOf(((BundleMeta) meta).getItems().size()));
                        }
                    }
                }
            });
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
        try {
            Class.forName("org.bukkit.inventory.meta.CrossbowMeta");
            ChestShop.registerListener(new Listener() {
                @EventHandler
                public void addCrossBowInfo(ItemInfoEvent event) {
                    if (event.getItem().hasItemMeta()) {
                        ItemMeta meta = event.getItem().getItemMeta();
                        if (meta instanceof CrossbowMeta && ((CrossbowMeta) meta).hasChargedProjectiles()) {
                            iteminfo_crossbow_projectiles.send(event.getSender());
                            for (ItemStack chargedProjectile : ((CrossbowMeta) meta).getChargedProjectiles()) {
                                ItemInfo.sendItemName(event.getSender(), chargedProjectile, iteminfo_crossbow_projectile);
                                ChestShop.callEvent(new ItemInfoEvent(event.getSender(), chargedProjectile));
                                event.getSender().sendMessage(ChatColor.GRAY + "---");
                            }
                        }
                    }
                }
            });
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
    }

    @EventHandler
    public static void addRepairCost(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof Repairable && ((Repairable) meta).getRepairCost() > 0) {
                iteminfo_repaircost.send(event.getSender(), "cost", String.valueOf(((Repairable) meta).getRepairCost()));
            }
        }
    }

    @EventHandler
    public static void addEnchantment(ItemInfoEvent event) {
        ItemStack item = event.getItem();
        CommandSender sender = event.getSender();

        for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
            sender.sendMessage(ChatColor.AQUA + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
        }

        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta) {
            for (Map.Entry<Enchantment, Integer> enchantment : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                sender.sendMessage(ChatColor.YELLOW + capitalizeFirstLetter(enchantment.getKey().getName(), '_') + ' ' + toRoman(enchantment.getValue()));
            }
        }
    }

    @EventHandler
    public static void addLeatherColor(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof LeatherArmorMeta) {
                Color color = ((LeatherArmorMeta) meta).getColor();
                iteminfo_leather_color.send(event.getSender(),
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
                iteminfo_recipes.send(event.getSender());
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
                iteminfo_tropical_fish.send(event.getSender(),
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
                    iteminfo_map_view.send(event.getSender(),
                            "id", String.valueOf(mapView.getId()),
                            "x", String.valueOf(mapView.getCenterX()),
                            "z", String.valueOf(mapView.getCenterZ()),
                            "world", mapView.getWorld() != null ? String.valueOf(mapView.getWorld().getName()) : "unknown",
                            "scale", capitalizeFirstLetter(mapView.getScale().name(), '_'),
                            "locked", String.valueOf(mapView.isLocked())
                    );
                }
                if (((MapMeta) meta).hasLocationName()) {
                    iteminfo_map_location.send(event.getSender(), "location", String.valueOf(((MapMeta) meta).getLocationName()));
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

        StringBuilder message = new StringBuilder(50);

        message.append(ChatColor.GRAY);

        message.append(capitalizeFirstLetter(item.getType().name(), '_')).append(" of ");
        message.append(capitalizeFirstLetter(potionMeta.getBasePotionData().getType().name(), '_')).append(' ');
        if (potionMeta.getBasePotionData().isUpgraded()) {
            message.append("II");
        } else if (potionMeta.getBasePotionData().isExtended()) {
            message.append("+");
        }

        CommandSender sender = event.getSender();

        sender.sendMessage(message.toString());

        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            sender.sendMessage(ChatColor.DARK_GRAY + capitalizeFirstLetter(effect.getType().getName(), '_') + ' ' + toTime(effect.getDuration() / 20));
        }
    }

    @EventHandler
    public static void addBookInfo(ItemInfoEvent event) {
        if (!event.getItem().hasItemMeta()) {
            return;
        }
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
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta.hasLore()) {
                iteminfo_lore.send(event.getSender(), "lore", String.join("\n", meta.getLore()));
            }
        }
    }
}
