package com.Acrobot.ChestShop.Metadata;

import com.Acrobot.Breeze.Database.Database;
import com.Acrobot.Breeze.Database.Row;
import com.Acrobot.Breeze.Database.Table;
import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.Encoding.Base64;
import com.Acrobot.ChestShop.ChestShop;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Saves items with Metadata in database, which allows for saving items on signs easily.
 *
 * @author Acrobot
 */
public class ItemDatabase {
    private Yaml yaml;
    private Table table;

    public ItemDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ChestShop.getBukkitLogger().severe("You haven't got any SQLite JDBC installed!");
        }

        Database database = new Database("jdbc:sqlite:" + ChestShop.loadFile("items.db").getAbsolutePath());
        yaml = new Yaml(new YamlConstructor(), new YamlRepresenter(), new DumperOptions());

        try {
            Statement statement = database.getConnection().createStatement();
            statement.executeUpdate("PRAGMA user_version = 1"); //We'll be able to change it later if we need to
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            table = database.getTable("items");
            table.create("id INTEGER PRIMARY KEY, code VARCHAR UNIQUE ON CONFLICT IGNORE");
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
            String code = Base64.encodeObject(yaml.dump(item));
            table.insertRow("null, '" + code + '\'');

            int id = Integer.parseInt(table.getRow("code='" + code + '\'').get("id"));
            return Base62.encode(id);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets an ItemStack from a item code
     *
     * @param code Item code
     * @return ItemStack represented by this code
     */
    public ItemStack getFromCode(String code) {
        try {
            Row row = table.getRow("id='" + Base62.decode(code) + '\'');

            if (row.getSize() == 0) {
                return null;
            }

            String serialized = row.get("code");

            return (ItemStack) yaml.load((String) Base64.decodeToObject(serialized));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
