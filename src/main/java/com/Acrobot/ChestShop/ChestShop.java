package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.Commands.Give;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Commands.Version;
import com.Acrobot.ChestShop.Commands.AccessToggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Migrations;
import com.Acrobot.ChestShop.Listeners.Block.BlockPlace;
import com.Acrobot.ChestShop.Listeners.Block.Break.ChestBreak;
import com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak;
import com.Acrobot.ChestShop.Listeners.Block.SignCreate;
import com.Acrobot.ChestShop.Listeners.Economy.ServerAccountCorrector;
import com.Acrobot.ChestShop.Listeners.Economy.TaxModule;
import com.Acrobot.ChestShop.Listeners.AuthMeChestShopListener;
import com.Acrobot.ChestShop.Listeners.GarbageTextListener;
import com.Acrobot.ChestShop.Listeners.Item.ItemMoveListener;
import com.Acrobot.ChestShop.Listeners.ItemInfoListener;
import com.Acrobot.ChestShop.Listeners.Modules.MetricsModule;
import com.Acrobot.ChestShop.Listeners.SignParseListener;
import com.Acrobot.ChestShop.Listeners.Modules.DiscountModule;
import com.Acrobot.ChestShop.Listeners.Modules.PriceRestrictionModule;
import com.Acrobot.ChestShop.Listeners.Player.*;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.CreationFeeGetter;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.MessageSender;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.ShopCreationLogger;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.SignSticker;
import com.Acrobot.ChestShop.Listeners.PostTransaction.*;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.*;
import com.Acrobot.ChestShop.Listeners.PreTransaction.*;
import com.Acrobot.ChestShop.Listeners.PreTransaction.ErrorMessageSender;
import com.Acrobot.ChestShop.Listeners.PreTransaction.PermissionChecker;
import com.Acrobot.ChestShop.Listeners.ShopRemoval.ShopRefundListener;
import com.Acrobot.ChestShop.Listeners.ShopRemoval.ShopRemovalLogger;
import com.Acrobot.ChestShop.Logging.FileFormatter;
import com.Acrobot.ChestShop.Metadata.ItemDatabase;
import com.Acrobot.ChestShop.Signs.RestrictedSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Updater.JenkinsBuildsNotifier;
import com.Acrobot.ChestShop.Updater.Updater;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Main file of the plugin
 *
 * @author Acrobot
 */
public class ChestShop extends JavaPlugin {
    private static ChestShop plugin;
    private static Server server;
    private static PluginDescriptionFile description;

    private static File dataFolder;
    private static ItemDatabase itemDatabase;

    private static Logger logger;
    private FileHandler handler;

    private List<PluginCommand> commands = new ArrayList<>();

    public ChestShop() {
        dataFolder = getDataFolder();
        logger = getLogger();
        description = getDescription();
        server = getServer();
        plugin = this;
    }

    @Override
    public void onLoad() {
        Dependencies.initializePlugins();
    }

    @Override
    public void onEnable() {
        turnOffDatabaseLogging();
        if (!handleMigrations()) {
            return;
        }

        registerCommand("iteminfo", new ItemInfo(), Permission.ITEMINFO);
        registerCommand("csVersion", new Version(), Permission.ADMIN);
        registerCommand("csMetrics", new com.Acrobot.ChestShop.Commands.Metrics(), Permission.ADMIN);
        registerCommand("csGive", new Give(), Permission.ADMIN);
        registerCommand("cstoggle", new Toggle(), Permission.NOTIFY_TOGGLE);
        registerCommand("csaccess", new AccessToggle(), Permission.ACCESS_TOGGLE);

        loadConfig();

        itemDatabase = new ItemDatabase();

        if (!Dependencies.loadPlugins()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerEvents();

        registerPluginMessagingChannels();

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

        startStatistics();
        startBuildNotificatier();
        startUpdater();
    }

    private void registerCommand(String name, CommandExecutor executor, Permission permission) {
        PluginCommand command = getCommand(name);
        command.setExecutor(executor);
        command.setPermission(permission.toString());
        commands.add(command);
    }

    public void loadConfig() {
        Configuration.pairFileAndClass(loadFile("config.yml"), Properties.class, getBukkitLogger());

        Messages.load();

        NameManager.load();

        commands.forEach(c -> c.setPermissionMessage(Messages.ACCESS_DENIED.getTextWithPrefix(null)));
    }

    private void turnOffDatabaseLogging() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("");

        loggerConfig.addFilter(new AbstractFilter() {
            @Override
            public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String msg, Object... params) {
                return filter(logger.getName(), level);
            }

            @Override
            public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Object msg, Throwable t) {
                return filter(logger.getName(), level);
            }

            @Override
            public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Message msg, Throwable t) {
                return filter(logger.getName(), level);
            }

            @Override
            public Result filter(LogEvent event) {
                return filter(event.getLoggerName(), event.getLevel());
            }

            private Result filter(String classname, Level level) {
                if (level.intLevel() <= Level.ERROR.intLevel() && !classname.contains("SqliteDatabaseType")) {
                    return Result.NEUTRAL;
                }

                if (classname.contains("SqliteDatabaseType") || classname.contains("TableUtils")) {
                    return Result.DENY;
                } else {
                    return Result.NEUTRAL;
                }
            }
        });
    }

    private boolean handleMigrations() {
        File versionFile = loadFile("version");
        YamlConfiguration previousVersion = YamlConfiguration.loadConfiguration(versionFile);

        if (previousVersion.get("version") == null) {
            previousVersion.set("version", Migrations.CURRENT_DATABASE_VERSION);

            try {
                previousVersion.save(versionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int lastVersion = previousVersion.getInt("version");
        int newVersion = Migrations.migrate(lastVersion);

        if (newVersion == -1) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Error while migrating! ChestShop can not run with a broken/outdated database...");
            plugin.getServer().getPluginManager().disablePlugin(this);
            return false;
        } else if (lastVersion != newVersion) {
            previousVersion.set("version", newVersion);

            try {
                previousVersion.save(versionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static File loadFile(String string) {
        File file = new File(dataFolder, string);

        return loadFile(file);
    }

    private static File loadFile(File file) {
        if (!file.exists()) {
            try {
                if (file.getParent() != null) {
                    file.getParentFile().mkdirs();
                }

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

        Toggle.clearToggledPlayers();

        if (handler != null) {
            handler.close();
            getLogger().removeHandler(handler);
        }
    }

    //////////////////    REGISTER EVENTS, SCHEDULER & STATS    ///////////////////////////
    private void registerEvents() {
        registerEvent(new com.Acrobot.ChestShop.Plugins.ChestShop()); //Chest protection

        registerEvent(new NameManager());

        registerPreShopCreationEvents();
        registerPreTransactionEvents();
        registerPostShopCreationEvents();
        registerPostTransactionEvents();
        registerShopRemovalEvents();

        registerModules();

        registerEvent(new SignBreak());
        registerEvent(new SignCreate());
        registerEvent(new ChestBreak());

        registerEvent(new BlockPlace());
        registerEvent(new PlayerConnect());
        registerEvent(new PlayerInteract());
        registerEvent(new PlayerInventory());
        registerEvent(new PlayerLeave());
        registerEvent(new PlayerTeleport());

        registerEvent(new SignParseListener());
        registerEvent(new ItemInfoListener());
        registerEvent(new GarbageTextListener());

        Plugin authMe = getServer().getPluginManager().getPlugin("AuthMe");
        if (authMe != null && authMe.isEnabled()) {
            registerEvent(new AuthMeChestShopListener());
        }

        registerEvent(new RestrictedSign());

        if (!Properties.TURN_OFF_HOPPER_PROTECTION) {
            registerEvent(new ItemMoveListener());
        }
    }

    private void registerShopRemovalEvents() {
        registerEvent(new ShopRefundListener());
        registerEvent(new ShopRemovalLogger());
    }

    private void registerPreShopCreationEvents() {
        if (Properties.BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE) {
            registerEvent(new PriceRatioChecker());
        }

        registerEvent(new ChestChecker());
        registerEvent(new ItemChecker());
        registerEvent(new MoneyChecker());
        registerEvent(new NameChecker());
        registerEvent(new com.Acrobot.ChestShop.Listeners.PreShopCreation.PermissionChecker());
        registerEvent(new com.Acrobot.ChestShop.Listeners.PreShopCreation.ErrorMessageSender());
        registerEvent(new PriceChecker());
        registerEvent(new QuantityChecker());
        registerEvent(new TerrainChecker());
    }

    private void registerPostShopCreationEvents() {
        registerEvent(new CreationFeeGetter());
        registerEvent(new MessageSender());
        registerEvent(new SignSticker());
        registerEvent(new ShopCreationLogger());
    }

    private void registerPreTransactionEvents() {
        if (Properties.ALLOW_PARTIAL_TRANSACTIONS) {
            registerEvent(new PartialTransactionModule());
        } else {
            registerEvent(new AmountAndPriceChecker());
        }

        registerEvent(new CreativeModeIgnorer());
        registerEvent(new ErrorMessageSender());
        registerEvent(new PermissionChecker());
        registerEvent(new PriceValidator());
        registerEvent(new ShopValidator());
        registerEvent(new SpamClickProtector());
        registerEvent(new StockFittingChecker());
    }

    private void registerPostTransactionEvents() {
        registerEvent(new EconomicModule());
        registerEvent(new EmptyShopDeleter());
        registerEvent(new ItemManager());
        registerEvent(new TransactionLogger());
        registerEvent(new TransactionMessageSender());
    }

    private void registerModules() {
        registerEvent(new DiscountModule());
        registerEvent(new MetricsModule());
        registerEvent(new PriceRestrictionModule());

        registerEconomicalModules();
    }

    private void registerEconomicalModules() {
        registerEvent(new ServerAccountCorrector());
        registerEvent(new TaxModule());
    }

    private void registerPluginMessagingChannels() {
        if (Properties.BUNGEECORD_MESSAGES) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }
    }

    public void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void startStatistics() {
        Metrics bStats = new Metrics(this, 1109);
        try {
            String dist = new JarFile(this.getFile()).getManifest().getMainAttributes().getValue("Distribution-Type");
            bStats.addCustomChart(new Metrics.SimplePie("distributionType", () -> dist));
        } catch (IOException ignored) {}

        bStats.addCustomChart(new Metrics.SingleLineChart("shopAccounts", NameManager::getAccountCount));
        bStats.addCustomChart(new Metrics.MultiLineChart("transactionCount", () -> ImmutableMap.of(
                "total", MetricsModule.getTotalTransactions(),
                "buy", MetricsModule.getBuyTransactions(),
                "sell", MetricsModule.getSellTransactions()
        )));bStats.addCustomChart(new Metrics.MultiLineChart("itemCount", () -> ImmutableMap.of(
                "total", MetricsModule.getTotalItemsCount(),
                "buy", MetricsModule.getSoldItemsCount(),
                "sell", MetricsModule.getBoughtItemsCount()
        )));

        bStats.addCustomChart(new Metrics.SimplePie("includeSettingsInMetrics", () -> Properties.INCLUDE_SETTINGS_IN_METRICS ? "enabled" : "disabled"));
        if (!Properties.INCLUDE_SETTINGS_IN_METRICS) return;

        bStats.addCustomChart(new Metrics.AdvancedBarChart("pluginProperties", () -> {
            Map<String, int[]> map = new LinkedHashMap<>();
            map.put("reverse-buttons", getChartArray(Properties.REVERSE_BUTTONS));
            map.put("shift-sells-in-stacks", getChartArray(Properties.SHIFT_SELLS_IN_STACKS));
            map.put("shift-sells-everything", getChartArray(Properties.SHIFT_SELLS_EVERYTHING));
            map.put("allow-sign-chest-open", getChartArray(!Properties.ALLOW_SIGN_CHEST_OPEN));
            map.put("remove-empty-shops", getChartArray(!Properties.REMOVE_EMPTY_SHOPS));
            map.put("remove-empty-chests", getChartArray(!Properties.REMOVE_EMPTY_CHESTS));
            map.put("uses-server-economy-account", getChartArray(!Properties.SERVER_ECONOMY_ACCOUNT.isEmpty()));
            map.put("uses-server-economy-account-uuid", getChartArray(!Properties.SERVER_ECONOMY_ACCOUNT_UUID.equals(new UUID(0, 0))));
            map.put("allow-multiple-shops-at-one-block", getChartArray(Properties.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK));
            map.put("allow-partial-transactions", getChartArray(Properties.ALLOW_PARTIAL_TRANSACTIONS));
            map.put("bungeecord-messages", getChartArray(Properties.BUNGEECORD_MESSAGES));
            map.put("log-to-console", getChartArray(Properties.LOG_TO_CONSOLE));
            map.put("log-to-file", getChartArray(Properties.LOG_TO_FILE));
            return map;
        }));
        bStats.addCustomChart(new Metrics.SimpleBarChart("shopContainers",
                () -> Properties.SHOP_CONTAINERS.stream().map(Material::name).collect(Collectors.toMap(k -> k, k -> 1))));
    }

    private int[] getChartArray(boolean value) {
        return new int[]{!value ? 1 : 0, value ? 0 : 1};
    }

    private static final int PROJECT_BUKKITDEV_ID = 31263;

    private void startUpdater() {
        if (Properties.TURN_OFF_UPDATES) {
            getLogger().info("Auto-updater is disabled. If you want the plugin to automatically download new releases then set 'TURN_OFF_UPDATES' to 'false' in your config.yml!");
            return;
        }

        new Updater(this, PROJECT_BUKKITDEV_ID, this.getFile(), Updater.UpdateType.DEFAULT, true);
    }

    private static final String PROJECT_JENKINS_JOB_URL = "https://ci.minebench.de/job/ChestShop-3/";

    private void startBuildNotificatier() {
        if (Properties.TURN_OFF_DEV_UPDATE_NOTIFIER) {
            return;
        }

        new JenkinsBuildsNotifier(this, PROJECT_JENKINS_JOB_URL);
    }

    ///////////////////////////////////////////////////////////////////////////////

    public static ItemDatabase getItemDatabase() {
        return itemDatabase;
    }

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

    public static List<String> getDependencies() {
        return description.getSoftDepend();
    }

    public static ChestShop getPlugin() {
        return plugin;
    }

    public static void registerListener(Listener listener) {
        plugin.registerEvent(listener);
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void sendBungeeMessage(String playerName, Messages.Message message, Map<String, String> replacementMap, String... replacements) {
        sendBungeeMessage(playerName, message.getComponents(null, true, replacementMap, replacements));
    }

    public static void sendBungeeMessage(String playerName, String message) {
        sendBungeeMessage(playerName, "Message", message);
    }

    public static void sendBungeeMessage(String playerName, BaseComponent[] message) {
        sendBungeeMessage(playerName, "MessageRaw", ComponentSerializer.toString(message));
    }

    private static void sendBungeeMessage(String playerName, String channel, String message) {
        if (Properties.BUNGEECORD_MESSAGES && !Bukkit.getOnlinePlayers().isEmpty()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(channel);
            out.writeUTF(playerName);
            out.writeUTF(message);

            Bukkit.getOnlinePlayers().iterator().next().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }
}
