package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.Version;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.DB.Generator;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Listeners.*;
import com.Acrobot.ChestShop.Logging.FileWriterQueue;
import com.Acrobot.ChestShop.Protection.MaskChest;
import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.Database;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main file of the plugin
 *
 * @author Acrobot
 */
public class ChestShop extends JavaPlugin {

    public static final File folder = new File("plugins/ChestShop");
    private static EbeanServer DB;

    private static PluginDescriptionFile description;
    private static Server server;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        //Set up our config file!
        Config.setUp();

        //Register our events
        blockBreak blockBreak = new blockBreak();

        description = this.getDescription();  //Description of the plugin
        server = getServer();          //Setting out server variable

        pm.registerEvent(Event.Type.BLOCK_BREAK, blockBreak, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, new blockPlace(), Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, new signChange(), Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, new playerInteract(), Event.Priority.Highest, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, new pluginEnable(), Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, new pluginDisable(), Event.Priority.Monitor, this);

        if(Config.getBoolean(Property.USE_BUILT_IN_PROTECTION)){
            pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, blockBreak, Event.Priority.Normal, this);
            pm.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, blockBreak, Event.Priority.Normal, this);
            pm.registerEvent(Event.Type.ENTITY_EXPLODE, new entityExplode(), Event.Priority.Normal, this);
        }

        if (Config.getBoolean(Property.LOG_TO_DATABASE) || Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) { //Now set up our database for storing transactions!
            setupDB();
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Queue(), 200L, 200L);

            if (Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) {
                getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Generator(), 300L, (long) Config.getDouble(Property.STATISTICS_PAGE_GENERATION_INTERVAL) * 20L);
            }
        }

        //Now set up our logging to file!
        if (Config.getBoolean(Property.LOG_TO_FILE)) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new FileWriterQueue(), 201L, 201L);
        }

        //And now for the chest masking
        if (Config.getBoolean(Property.MASK_CHESTS_AS_OTHER_BLOCKS)) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new MaskChest(), 40L, 40L);
        }


        //Register our commands!
        getCommand("iteminfo").setExecutor(new ItemInfo());
        getCommand("csVersion").setExecutor(new Version());

        System.out.println('[' + getPluginName() + "] version " + getVersion() + " initialized!");
    }

    public void onDisable() {
        System.out.println('[' + getPluginName() + "] version " + getVersion() + " shutting down!");
    }

    /////////////////////   DATABASE    STUFF      ////////////////////////////////
    private static Configuration getBukkitConfig() {
        Configuration config = new Configuration(new File("bukkit.yml"));
        config.load();
        return config;
    }

    private static Database database;

    private void setupDB() {
        database = new Database(this) {
            protected java.util.List<Class<?>> getDatabaseClasses() {
                List<Class<?>> list = new ArrayList<Class<?>>();
                list.add(Transaction.class);
                return list;
            }
        };

        Configuration config = getBukkitConfig();

        database.initializeDatabase(
                config.getString("database.driver"),
                config.getString("database.url"),
                config.getString("database.username"),
                config.getString("database.password"),
                config.getString("database.isolation"),
                false,
                true
        );

        DB = database.getDatabase();
    }

    @Override
    public EbeanServer getDatabase() {
        return database.getDatabase();
    }
    ///////////////////////////////////////////////////////////////////////////////

    public static Server getBukkitServer() {
        return server;
    }

    public static String getVersion() {
        return description.getVersion();
    }

    public static String getPluginName() {
        return description.getName();
    }

    public static EbeanServer getDB() {
        return DB;
    }
}
