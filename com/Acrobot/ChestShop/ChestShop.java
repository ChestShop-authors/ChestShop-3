package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.Version;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.DB.Generator;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Listeners.Block.BlockPlace;
import com.Acrobot.ChestShop.Listeners.Block.Break.ChestBreak;
import com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak;
import com.Acrobot.ChestShop.Listeners.Block.SignChange;
import com.Acrobot.ChestShop.Listeners.ItemInfoListener;
import com.Acrobot.ChestShop.Listeners.Player.PlayerConnect;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInteract;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInventory;
import com.Acrobot.ChestShop.Listeners.Player.ShortNameSaver;
import com.Acrobot.ChestShop.Listeners.PostTransaction.*;
import com.Acrobot.ChestShop.Listeners.PreTransaction.*;
import com.Acrobot.ChestShop.Listeners.ShopRefundListener;
import com.Acrobot.ChestShop.Logging.FileFormatter;
import com.Acrobot.ChestShop.Signs.RestrictedSign;
import com.Acrobot.ChestShop.Utils.uName;
import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.Database;
import com.nijikokun.register.payment.forChestShop.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Main file of the plugin
 *
 * @author Acrobot
 */
public class ChestShop extends JavaPlugin {
    public static File dataFolder = new File("plugins/ChestShop");

    private static EbeanServer database;
    private static PluginDescriptionFile description;
    private static Server server;
    private static Logger logger;
    private static ChestShop plugin;

    private FileHandler handler;

    public void onEnable() {
        plugin = this;
        logger = getLogger();
        dataFolder = getDataFolder();
        description = getDescription();
        server = getServer();

        Configuration.pairFileAndClass(loadFile("config.yml"), Properties.class);
        Configuration.pairFileAndClass(loadFile("local.yml"), Messages.class);

        uName.file = loadFile("longName.storage");
        uName.load();

        Methods.setPreferred(Properties.PREFERRED_ECONOMY_PLUGIN);

        Dependencies.load();

        registerEvents();

        if (Properties.LOG_TO_DATABASE || Properties.GENERATE_STATISTICS_PAGE) {
            setupDB();
        }
        if (Properties.GENERATE_STATISTICS_PAGE) {
            File htmlFolder = new File(Properties.STATISTICS_PAGE_PATH);
            scheduleTask(new Generator(htmlFolder), 300L, Properties.STATISTICS_PAGE_GENERATION_INTERVAL * 20L);
        }
        if (Properties.LOG_TO_FILE) {
            File log = loadFile("ChestShop.log");

            FileHandler handler = loadHandler(log.getAbsolutePath());
            handler.setFormatter(new FileFormatter());

            this.handler = handler;
            logger.addHandler(handler);
        }
        if (!Properties.LOG_TO_CONSOLE) {
            logger.setUseParentHandlers(false);
        }

        getCommand("iteminfo").setExecutor(new ItemInfo());
        getCommand("csVersion").setExecutor(new Version());

        startStatistics();
    }

    public static File loadFile(String string) {
        File file = new File(dataFolder, string);

        return loadFile(file);
    }

    private static File loadFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private static FileHandler loadHandler(String path) {
        FileHandler handler = null;

        try {
            handler = new FileHandler(path, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return handler;
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);

        if (handler != null) {
            handler.close();
            getLogger().removeHandler(handler);
        }
    }

    //////////////////    REGISTER EVENTS, SCHEDULER & STATS    ///////////////////////////
    private void registerEvents() {
        registerEvent(new com.Acrobot.ChestShop.Plugins.ChestShop()); //Chest protection

        registerEvent(new SignBreak());
        registerEvent(new ChestBreak());

        registerEvent(new BlockPlace());
        registerEvent(new SignChange());
        registerEvent(new PlayerConnect());
        registerEvent(new PlayerInteract());
        registerEvent(new PlayerInventory());

        registerEvent(new ItemInfoListener());

        registerEvent(new EmptyShopDeleter());
        registerEvent(new TransactionLogger());
        registerEvent(new TransactionMessageSender());

        registerEvent(new EconomicModule());
        registerEvent(new ItemManager());

        registerEvent(new CreativeModeIgnorer());
        registerEvent(new PermissionChecker());
        registerEvent(new PriceValidator());
        registerEvent(new ShopValidator());
        registerEvent(new StockFittingChecker());
        registerEvent(new ErrorMessageSender());
        registerEvent(new SpamClickProtector());

        registerEvent(new RestrictedSign());
        registerEvent(new DiscountModule());
        registerEvent(new ShopRefundListener());

        if (Properties.ALLOW_PARTIAL_TRANSACTIONS) {
            registerEvent(new PartialTransactionModule());
        } else {
            registerEvent(new AmountAndPriceChecker());
        }

        registerEvent(new ShortNameSaver());
    }

    public void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void scheduleTask(Runnable runnable, long startTime, long repetetionTime) {
        server.getScheduler().scheduleAsyncRepeatingTask(this, runnable, startTime, repetetionTime);
    }

    private void startStatistics() {
        try {
            new Metrics(this).start();
        } catch (IOException ex) {
            ChestShop.getBukkitLogger().severe("There was an error while submitting statistics.");
        }
    }

    /////////////////////   DATABASE    STUFF      ////////////////////////////////
    private void setupDB() {
        Database DB;

        DB = new Database(this) {
            protected java.util.List<Class<?>> getDatabaseClasses() {
                List<Class<?>> list = new ArrayList<Class<?>>();
                list.add(Transaction.class);
                return list;
            }
        };

        FileConfiguration config = getConfig();

        DB.initializeDatabase(
                config.getString("database.driver"),
                config.getString("database.url"),
                config.getString("database.username"),
                config.getString("database.password"),
                config.getString("database.isolation")
        );

        database = DB.getDatabase();
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Queue(), 200L, 200L);
    }

    @Override
    public EbeanServer getDatabase() {
        return database;
    }
    ///////////////////////////////////////////////////////////////////////////////

    public static File getFolder() {
        return dataFolder;
    }

    public static Logger getBukkitLogger() {
        return logger;
    }

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
        return database;
    }

    public static List<String> getDependencies() {
        return description.getSoftDepend();
    }

    public static void registerListener(Listener listener) {
        plugin.registerEvent(listener);
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
