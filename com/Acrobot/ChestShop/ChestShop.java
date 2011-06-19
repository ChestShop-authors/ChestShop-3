package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.Options;
import com.Acrobot.ChestShop.Commands.Version;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.DB.Generator;
import com.Acrobot.ChestShop.DB.Queue;
import com.Acrobot.ChestShop.DB.Transaction;
import com.Acrobot.ChestShop.Listeners.*;
import com.Acrobot.ChestShop.Logging.FileWriterQueue;
import com.Acrobot.ChestShop.Logging.Logging;
import com.avaje.ebean.EbeanServer;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main file of the plugin
 *
 * @author Acrobot
 */
public class ChestShop extends JavaPlugin {

    private final pluginEnable pluginEnable = new pluginEnable();
    private final blockBreak blockBreak = new blockBreak();
    private final blockPlace blockPlace = new blockPlace();
    private final signChange signChange = new signChange();
    private final pluginDisable pluginDisable = new pluginDisable();
    private final playerInteract playerInteract = new playerInteract();

    public static File folder;
    public static EbeanServer db;

    private static PluginDescriptionFile desc;
    private static Server server;

    public static String mainWorldName;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        //Register our events
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockBreak, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockPlace, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, signChange, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Highest, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginEnable, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, pluginDisable, Event.Priority.Monitor, this);

        desc = this.getDescription();
        server = getServer();
        mainWorldName = server.getWorlds().get(0).getName();

        //Yep, set up our folder!
        folder = getDataFolder();

        //Set up our config file!
        Config.setUp();


        //Now set up our database for storing transactions!
        setupDBfile();
        if (Config.getBoolean(Property.USE_DATABASE)) {
            setupDB();
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Queue(), 200L, 200L);

            if (Config.getBoolean(Property.GENERATE_STATISTICS_PAGE)) {
                getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Generator(), 300L, 300L);
            }
            db = getDatabase();
        }

        //Now set up our logging to file!
        if (Config.getBoolean(Property.LOG_TO_FILE)) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new FileWriterQueue(), 201L, 201L);
        }


        //Register our commands!
        getCommand("iteminfo").setExecutor(new ItemInfo());
        getCommand("chestOptions").setExecutor(new Options());
        getCommand("csVersion").setExecutor(new Version());

        System.out.println('[' + desc.getName() + "] version " + desc.getVersion() + " initialized!");
    }

    public void onDisable() {
        System.out.println('[' + desc.getName() + "] version " + desc.getVersion() + " shutting down!");
    }

    /////////////////////   DATABASE    STUFF      ////////////////////////////////
    private void setupDB() {
        try {
            getDatabase().find(Transaction.class).findRowCount();
        } catch (PersistenceException pe) {
            Logging.log("Installing database for " + getPluginName());
            installDDL();
        }
    }

    private static void setupDBfile() {
        File file = new File("ebean.properties");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                Logging.log("Failed to create ebean.properties file!");
            }
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Transaction.class);
        return list;
    }
    ///////////////////////////////////////////////////////////////////////////////

    public static Server getBukkitServer() {
        return server;
    }

    public static String getVersion() {
        return desc.getVersion();
    }

    public static String getPluginName() {
        return desc.getName();
    }

    public static EbeanServer getDB() {
        return db;
    }
}
