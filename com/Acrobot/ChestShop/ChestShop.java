package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.Version;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.ConfigObject;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.DB.Generator;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Listeners.*;
import com.Acrobot.ChestShop.Logging.FileWriterQueue;
import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.Database;
import org.bukkit.Server;
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

    public static File folder = new File("plugins/ChestShop");
    public static final String chatPrefix = "[ChestShop] ";
    private static EbeanServer DB;

    private static PluginDescriptionFile description;
    private static Server server;

    public static PluginManager pm;

    public void onEnable() {
        pm = getServer().getPluginManager();
        folder = getDataFolder();

        //Set up our config file!
        Config.setup(new ConfigObject());

        //Register our events
        registerEvents();

        description = this.getDescription();  //Description of the plugin
        server = getServer();          //Setting out server variable

        pluginEnable.initializePlugins();

        if (Config.getBoolean(Property.LOG_TO_DATABASE) || Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) setupDB();
        if (Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) scheduleTask(new Generator(), 300L, (long) Config.getDouble(Property.STATISTICS_PAGE_GENERATION_INTERVAL) * 20L);
        if (Config.getBoolean(Property.LOG_TO_FILE)) scheduleTask(new FileWriterQueue(), 201L, 201L);
        //if (Config.getBoolean(Property.MASK_CHESTS_AS_OTHER_BLOCKS)) scheduleTask(new MaskChest(), 40L, 40L); //Disabled due to bug //TODO Fix that
        playerInteract.interval = Config.getInteger(Property.SHOP_INTERACTION_INTERVAL);

        //Register our commands!
        getCommand("iteminfo").setExecutor(new ItemInfo());
        getCommand("csVersion").setExecutor(new Version());

        //Start the statistics pinger
        startStatistics();

        System.out.println('[' + getPluginName() + "] version " + getVersion() + " initialized!");
    }

    public void onDisable() {
        System.out.println('[' + getPluginName() + "] version " + getVersion() + " shutting down!");
    }

    //////////////////    REGISTER EVENTS, SCHEDULER & STATS    ///////////////////////////
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(new blockBreak(), this);
        pm.registerEvents(new blockPlace(), this);
        pm.registerEvents(new signChange(), this);
        pm.registerEvents(new playerInteract(), this);
        pm.registerEvents(new entityExplode(), this);
    }

    private void scheduleTask(Runnable runnable, long startTime, long repetetionTime) {
        server.getScheduler().scheduleAsyncRepeatingTask(this, runnable, startTime, repetetionTime);
    }

    private void startStatistics(){
        try{
            new Metrics().beginMeasuringPlugin(this);
        } catch (Exception ex){
            System.out.println(chatPrefix + "There was an error while submitting statistics.");
        }
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
                config.getString("database.isolation")
        );

        DB = database.getDatabase();
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Queue(), 200L, 200L);
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

    public static ArrayList getDependencies() {
        return (ArrayList) description.getSoftDepend();
    }
}
