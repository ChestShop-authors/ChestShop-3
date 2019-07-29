package com.Acrobot.ChestShop.Metadata;

import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.Encoding.Base64;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Database.Item;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Saves items with Metadata in database, which allows for saving items on signs easily.
 *
 * @author Acrobot
 */
public class ItemDatabase {
    private Dao<Item, Integer> itemDao;

    private final Yaml yaml;

    public ItemDatabase() {
        yaml = new Yaml(new YamlBukkitConstructor(), new YamlRepresenter(), new DumperOptions());

        try {
            itemDao = DaoCreator.getDaoAndCreateTable(Item.class);
            handleMetadataUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleMetadataUpdate() {
        File configFile = ChestShop.loadFile("version");
        YamlConfiguration versionConfig = YamlConfiguration.loadConfiguration(configFile);

        int previousVersion = versionConfig.getInt("metadata-version", -1);
        int newVersion = getCurrentMetadataVersion();
        if (previousVersion < newVersion) {
            if (updateMetadataVersion(previousVersion, newVersion)) {
                versionConfig.set("metadata-version", newVersion);
                try {
                    versionConfig.save(configFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ChestShop.getBukkitLogger().log(Level.WARNING, "Error while updating Item Metadata database! While the plugin will still run it will work less efficiently.");
            }
        }
    }

    private int getCurrentMetadataVersion() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("GetCurrentMetadataVersion");
        item.setItemMeta(meta);
        Map<String, Object> serialized = item.serialize();
        return (int) serialized.getOrDefault("v", -1);
    }

    private boolean updateMetadataVersion(int previousVersion, int newVersion) {
        if (previousVersion > -1) {
            ChestShop.getBukkitLogger().info("Data version change detected! Previous version was " + previousVersion);
        }
        ChestShop.getBukkitLogger().info("Updating Item Metadata database to data version " + newVersion + "...");

        AtomicInteger i = new AtomicInteger();
        AtomicInteger updated = new AtomicInteger();
        CloseableIterator<Item> it = itemDao.iterator();

        long start = System.currentTimeMillis();
        try {
            itemDao.callBatchTasks(() -> {
                while (it.hasNext()) {
                    i.getAndIncrement();
                    Item item = it.next();

                    try {
                        String serialized = (String) Base64.decodeToObject(item.getBase64ItemCode());
                        if (previousVersion < 0 || !serialized.contains("\nv: " + newVersion + "\n")) { // Hacky way to quickly check the version as it's not too big of an issue if some items don't convert
                            try {
                                ItemStack itemStack = yaml.loadAs(serialized, ItemStack.class);
                                item.setBase64ItemCode(Base64.encodeObject(yaml.dump(itemStack)));
                                itemDao.update(item);
                                updated.getAndIncrement();
                            } catch (YAMLException e) {
                                ChestShop.getBukkitLogger().log(Level.SEVERE, "YAML of the item with ID " + Base62.encode(item.getId()) + " (" + item.getId() + ") is corrupted: \n" + serialized);
                            }
                        }
                    } catch (IOException | ClassNotFoundException | SQLException e) {
                        ChestShop.getBukkitLogger().log(Level.SEVERE, "Unable to convert item with ID " + Base62.encode(item.getId()) + " (" + item.getId() + ")", e);
                    } catch (StackOverflowError e) {
                        ChestShop.getBukkitLogger().log(Level.SEVERE, "Item with ID " + Base62.encode(item.getId()) + " (" + item.getId() + ") is corrupted. Sorry :(");
                    }
                    if (i.get() % 1000 == 0) {
                        ChestShop.getBukkitLogger().info("Checked " + i + " items. Updated " + updated + "...");
                    }
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            it.closeQuietly();
        }

        ChestShop.getBukkitLogger().info("Finished updating database in " + (System.currentTimeMillis() - start) / 1000.0 + "s. " +
                updated + " items out of " + i + " were updated!");
        return true;
    }

    /**
     * Gets the item code for this item
     *
     * @param item Item
     * @return Item code for this item
     */
    public String getItemCode(ItemStack item) {
        try {
            ItemStack clone = new ItemStack(item);
            clone.setAmount(1);
            clone.setDurability((short) 0);

            String dumped = yaml.dump(clone);
            ItemStack loadedItem = yaml.loadAs(dumped, ItemStack.class);
            if (!loadedItem.isSimilar(item)) {
                dumped = yaml.dump(loadedItem);
            }
            String code = Base64.encodeObject(dumped);

            Item itemEntity = itemDao.queryBuilder().where().eq("code", new SelectArg(code)).queryForFirst();
            if (itemEntity == null) {
                itemEntity = new Item(code);
                itemDao.create(itemEntity);
            }
            return Base62.encode(itemEntity.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets an ItemStack from a item code
     *
     * @param code Item code
     * @return ItemStack represented by this code
     */
    public ItemStack getFromCode(String code)
    {
        // TODO java.lang.StackOverflowError - http://pastebin.com/eRD8wUFM - Corrupt item DB?

        int id = Base62.decode(code);
        try {
            Item item = itemDao.queryBuilder().where().eq("id", new SelectArg(id)).queryForFirst();

            if (item == null) {
                return null;
            }

            String serialized = item.getBase64ItemCode();

            try {
                return yaml.loadAs((String) Base64.decodeToObject(serialized), ItemStack.class);
            } catch (YAMLException e) {
                ChestShop.getBukkitLogger().log(Level.SEVERE, "YAML of the item with ID " + Base62.encode(item.getId()) + " (" + item.getId() + ") is corrupted: \n" + serialized);
            }
        } catch (IOException | ClassNotFoundException | SQLException | YAMLException e) {
            ChestShop.getBukkitLogger().log(Level.SEVERE, "Unable to load item with ID " + code + " (" + id + ")", e);
        } catch (StackOverflowError e) {
            ChestShop.getBukkitLogger().log(Level.SEVERE, "Item with ID " + code + " (" + id + ") is corrupted. Sorry :(");
        }

        return null;
    }

    private class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }

    }
}
