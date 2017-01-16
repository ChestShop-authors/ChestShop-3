package com.Acrobot.ChestShop.Metadata;

import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.Encoding.Base64;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Database.Item;
import com.j256.ormlite.dao.Dao;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.sql.SQLException;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

            String code = Base64.encodeObject(yaml.dump(clone));
            Item itemEntity = itemDao.queryBuilder().where().eq("code", code).queryForFirst();

            if (itemEntity != null) {
                return Base62.encode(itemEntity.getId());
            }

            itemEntity = new Item(code);

            itemDao.create(itemEntity);

            int id = itemEntity.getId();

            return Base62.encode(id);
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

        try {
            int id = Base62.decode(code);
            Item item = itemDao.queryBuilder().where().eq("id", id).queryForFirst();

            if (item == null) {
                return null;
            }

            String serialized = item.getBase64ItemCode();

            return yaml.loadAs((String) Base64.decodeToObject(serialized), ItemStack.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }

    }
}
