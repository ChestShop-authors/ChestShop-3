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
import com.Acrobot.ChestShop.Shop.ShopManagement;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.Database;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

        warnAboutSpawnProtection();
        warnAboutOldBukkit();

        if (Config.getBoolean(Property.LOG_TO_DATABASE) || Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) setupDB();
        if (Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) scheduleTask(new Generator(), 300L, (long) Config.getDouble(Property.STATISTICS_PAGE_GENERATION_INTERVAL) * 20L);
        if (Config.getBoolean(Property.LOG_TO_FILE)) scheduleTask(new FileWriterQueue(), 201L, 201L);
        playerInteract.interval = Config.getInteger(Property.SHOP_INTERACTION_INTERVAL);

        //Register our commands!
        getCommand("iteminfo").setExecutor(new ItemInfo());
        getCommand("csVersion").setExecutor(new Version());

        //Start the statistics pinger
        startStatistics();
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
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

    private void startStatistics() {
        try {
            new Metrics(this).start();
        } catch (Exception ex) {
            System.err.println(chatPrefix + "There was an error while submitting statistics.");
        }
    }

    /////////////////////   WARN  ///////////////////////////
    private static void warnAboutSpawnProtection() {
        if (getBukkitConfig().getInt("settings.spawn-radius") > 0)
            System.err.println(ChestShop.chatPrefix + "WARNING! Your spawn-radius in bukkit.yml isn't set to 0! " +
                    "You won't be able to sell to shops built near spawn!");
    }

    private static void warnAboutOldBukkit() {
        String split[] = Bukkit.getBukkitVersion().split("-R");
        if (split[0].equals("1.1") && split.length > 1 && uNumber.isInteger(split[1]) && (Integer.parseInt(split[1])) < 7) {
            System.err.println(ChestShop.chatPrefix + "Your CraftBukkit version is outdated! Use at least 1.1-R7 or 1.2.3-R0!");
            ShopManagement.useOldChest = true;
        }
    }

    /////////////////////   DATABASE    STUFF      ////////////////////////////////
    private static YamlConfiguration getBukkitConfig() {
        return YamlConfiguration.loadConfiguration(new File("bukkit.yml"));
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

        YamlConfiguration config = getBukkitConfig();

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

    public static List getDependencies() {
        return (List) description.getSoftDepend();
    }
}
