package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.Commands.Give;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.ShopInfo;
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
import com.Acrobot.ChestShop.Listeners.Economy.EconomyAdapter;
import com.Acrobot.ChestShop.Listeners.Economy.ServerAccountCorrector;
import com.Acrobot.ChestShop.Listeners.Economy.TaxModule;
import com.Acrobot.ChestShop.Listeners.AuthMeChestShopListener;
import com.Acrobot.ChestShop.Listeners.GarbageTextListener;
import com.Acrobot.ChestShop.Listeners.Item.ItemMoveListener;
import com.Acrobot.ChestShop.Listeners.Item.ItemStringListener;
import com.Acrobot.ChestShop.Listeners.ItemInfoListener;
import com.Acrobot.ChestShop.Listeners.Modules.ItemAliasModule;
import com.Acrobot.ChestShop.Listeners.Modules.MetricsModule;
import com.Acrobot.ChestShop.Listeners.Modules.StockCounterModule;
import com.Acrobot.ChestShop.Listeners.ShopInfoListener;
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

import jdk.internal.joptsimple.util.KeyValuePair;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
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
import org.bstats.charts.AdvancedBarChart;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SimpleBarChart;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static Metrics bStats;

    private static BukkitAudiences audiences;

    private static File dataFolder;
    private static ItemDatabase itemDatabase;

    private static Logger logger;
    private static Logger shopLogger;
    private FileHandler handler;

    private List<PluginCommand> commands = new ArrayList<>();

    public ChestShop() {
        dataFolder = getDataFolder();
        logger = getLogger();
        shopLogger = Logger.getLogger("ChestShop Shops");
        shopLogger.setParent(logger);
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
        bStats = new Metrics(this, 1109);
        audiences = BukkitAudiences.create(this);
        turnOffDatabaseLogging();
        if (!handleMigrations()) {
            return;
        }

        registerCommand("iteminfo", new ItemInfo(), Permission.ITEMINFO);
        registerCommand("shopinfo", new ShopInfo(), Permission.SHOPINFO);
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

        startStatistics();
        startBuildNotificatier();
        startUpdater();
    }

    private void registerCommand(String name, CommandExecutor executor, Permission permission) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            command.setPermission(permission.toString());
            commands.add(command);
        }
    }

    public void loadConfig() {
        Configuration.pairFileAndClass(loadFile("config.yml"), Properties.class, getBukkitLogger());

        Messages.load();

        NameManager.load();

        commands.forEach(c -> c.setPermissionMessage(Messages.ACCESS_DENIED.getTextWithPrefix(null)));

        if (handler != null) {
            shopLogger.removeHandler(handler);
        }

        if (Properties.LOG_TO_FILE) {
            if (handler == null) {
                File log = loadFile("ChestShop.log");

                handler = loadHandler(log.getAbsolutePath());
                handler.setFormatter(new FileFormatter());
            }
            shopLogger.addHandler(handler);
        }

        shopLogger.setUseParentHandlers(Properties.LOG_TO_CONSOLE);
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
                getLogger().log(java.util.logging.Level.SEVERE, "Unable to save new database version " + Migrations.CURRENT_DATABASE_VERSION, e);
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
                getLogger().log(java.util.logging.Level.SEVERE, "Unable to save new database version " + newVersion, e);
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
                getBukkitLogger().log(java.util.logging.Level.SEVERE, "Unable to load file " + file.getName(), e);
            }
        }

        return file;
    }

    private static FileHandler loadHandler(String path) {
        FileHandler handler = null;

        try {
            handler = new FileHandler(path, true);
        } catch (IOException ex) {
            getBukkitLogger().log(java.util.logging.Level.SEVERE, "Unable to load handler " + path, ex);
        }

        return handler;
    }

    public void onDisable() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}

        Toggle.clearToggledPlayers();

        if (handler != null) {
            handler.close();
            getLogger().removeHandler(handler);
        }
    }

    //////////////////    REGISTER EVENTS, SCHEDULER & STATS    ///////////////////////////
    private void registerEvents() {
        registerEvent(new com.Acrobot.ChestShop.Plugins.ChestShop()); //Chest protection

        registerEvent(new Dependencies());

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
        registerEvent(new ItemStringListener());
        registerEvent(new ItemInfoListener());
        registerEvent(new ShopInfoListener());
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

        registerEvent(new InvalidNameIgnorer());
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
        registerEvent(new ItemAliasModule());
        registerEvent(new DiscountModule());
        registerEvent(new MetricsModule());
        registerEvent(new PriceRestrictionModule());
        registerEvent(new StockCounterModule());

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
        try (JarFile jarFile = new JarFile(this.getFile())) {
            String dist = jarFile.getManifest().getMainAttributes().getValue("Distribution-Type");
            bStats.addCustomChart(new SimplePie("distributionType", () -> dist));
        } catch (IOException ignored) {}

        String serverVersion = getServer().getBukkitVersion().split("-")[0];
        bStats.addCustomChart(createStaticDrilldownStat("versionMcSelf", serverVersion, getDescription().getVersion()));
        bStats.addCustomChart(createStaticDrilldownStat("versionSelfMc", getDescription().getVersion(), serverVersion));

        bStats.addCustomChart(createStaticDrilldownStat("serverTypeVersionSelf", getServer().getName(), getDescription().getVersion()));
        bStats.addCustomChart(createStaticDrilldownStat("versionSelfServerType", getDescription().getVersion(), getServer().getName()));

        bStats.addCustomChart(createStaticDrilldownStat("versionMcServerType", serverVersion, getServer().getName()));
        bStats.addCustomChart(createStaticDrilldownStat("serverTypeVersionMc", getServer().getName(), serverVersion));

        String javaVersion = System.getProperty("java.version");
        bStats.addCustomChart(createStaticDrilldownStat("versionJavaSelf", javaVersion, getDescription().getVersion()));
        bStats.addCustomChart(createStaticDrilldownStat("versionSelfJava", getDescription().getVersion(), javaVersion));

        bStats.addCustomChart(createStaticDrilldownStat("versionJavaMc", javaVersion, serverVersion));
        bStats.addCustomChart(createStaticDrilldownStat("versionMcJava", serverVersion, javaVersion));

        bStats.addCustomChart(new SingleLineChart("shopAccounts", NameManager::getAccountCount));
        bStats.addCustomChart(new MultiLineChart("transactionCount", () -> ImmutableMap.of(
                "total", MetricsModule.getTotalTransactions(),
                "buy", MetricsModule.getBuyTransactions(),
                "sell", MetricsModule.getSellTransactions()
        )));
        bStats.addCustomChart(new MultiLineChart("itemCount", () -> ImmutableMap.of(
                "total", MetricsModule.getTotalItemsCount(),
                "buy", MetricsModule.getSoldItemsCount(),
                "sell", MetricsModule.getBoughtItemsCount()
        )));

        bStats.addCustomChart(new SimplePie("includeSettingsInMetrics", () -> Properties.INCLUDE_SETTINGS_IN_METRICS ? "enabled" : "disabled"));
        if (!Properties.INCLUDE_SETTINGS_IN_METRICS) return;

        bStats.addCustomChart(new SimplePie("ensure-correct-playerid", () -> Properties.ENSURE_CORRECT_PLAYERID ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("allow-sign-chest-open", () -> Properties.ALLOW_SIGN_CHEST_OPEN ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("uses-server-economy-account", () -> !Properties.SERVER_ECONOMY_ACCOUNT.isEmpty() ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("uses-server-economy-account-uuid", () -> !Properties.SERVER_ECONOMY_ACCOUNT_UUID.equals(new UUID(0, 0)) ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("allow-partial-transactions", () -> Properties.ALLOW_PARTIAL_TRANSACTIONS ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("bungeecord-messages", () -> Properties.BUNGEECORD_MESSAGES ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("allow-multiple-shops-at-one-block", () -> Properties.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("allow-partial-transactions", () -> Properties.ALLOW_PARTIAL_TRANSACTIONS ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("log-to-console", () -> Properties.LOG_TO_CONSOLE ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("log-to-file", () -> Properties.LOG_TO_FILE ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("auto-update", () -> !Properties.TURN_OFF_UPDATES ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("release-notifications", () -> !Properties.TURN_OFF_UPDATE_NOTIFIER ? "enabled" : "disabled"));
        bStats.addCustomChart(new SimplePie("dev-build-notifications", () -> !Properties.TURN_OFF_DEV_UPDATE_NOTIFIER ? "enabled" : "disabled"));

        bStats.addCustomChart(new AdvancedBarChart("pluginProperties", () -> {
            Map<String, int[]> map = new LinkedHashMap<>();
            map.put("ensure-correct-playerid", getChartArray(Properties.ENSURE_CORRECT_PLAYERID));
            map.put("reverse-buttons", getChartArray(Properties.REVERSE_BUTTONS));
            map.put("shift-sells-in-stacks", getChartArray(Properties.SHIFT_SELLS_IN_STACKS));
            map.put("shift-sells-everything", getChartArray(Properties.SHIFT_SELLS_EVERYTHING));
            map.put("allow-sign-chest-open", getChartArray(!Properties.ALLOW_SIGN_CHEST_OPEN));
            map.put("sign-dying", getChartArray(!Properties.SIGN_DYING));
            map.put("remove-empty-shops", getChartArray(!Properties.REMOVE_EMPTY_SHOPS));
            map.put("remove-empty-chests", getChartArray(!Properties.REMOVE_EMPTY_CHESTS));
            map.put("uses-server-economy-account", getChartArray(!Properties.SERVER_ECONOMY_ACCOUNT.isEmpty()));
            map.put("uses-server-economy-account-uuid", getChartArray(!Properties.SERVER_ECONOMY_ACCOUNT_UUID.equals(new UUID(0, 0))));
            map.put("allow-multiple-shops-at-one-block", getChartArray(Properties.ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK));
            map.put("allow-partial-transactions", getChartArray(Properties.ALLOW_PARTIAL_TRANSACTIONS));
            map.put("bungeecord-messages", getChartArray(Properties.BUNGEECORD_MESSAGES));
            map.put("log-to-console", getChartArray(Properties.LOG_TO_CONSOLE));
            map.put("log-to-file", getChartArray(Properties.LOG_TO_FILE));
            map.put("auto-update", getChartArray(!Properties.TURN_OFF_UPDATES));
            map.put("release-notifications", getChartArray(!Properties.TURN_OFF_UPDATE_NOTIFIER));
            map.put("dev-build-notifications", getChartArray(!Properties.TURN_OFF_DEV_UPDATE_NOTIFIER));
            return map;
        }));
        bStats.addCustomChart(new SimpleBarChart("shopContainers",
                () -> Properties.SHOP_CONTAINERS.stream().map(Material::name).collect(Collectors.toMap(k -> k, k -> 1))));
    }

    public static DrilldownPie createStaticDrilldownStat(String statId, String value1, String value2) {
        final Map<String, Map<String, Integer>> map = ImmutableMap.of(value1, ImmutableMap.of(value2, 1));
        return new DrilldownPie(statId, () -> map);
    }

    public static DrilldownPie createStaticDrilldownStat(String statId, Callable<EconomyAdapter.ProviderInfo> callableProviderInfo) {
        return new DrilldownPie(statId, () -> {
            EconomyAdapter.ProviderInfo providerInfo = callableProviderInfo.call();
            return ImmutableMap.of(providerInfo.getName(), ImmutableMap.of(providerInfo.getVersion(), 1));
        });
    }

    private int[] getChartArray(boolean value) {
        return new int[]{!value ? 1 : 0, value ? 0 : 1};
    }

    private static final int PROJECT_BUKKITDEV_ID = 31263;

    private void startUpdater() {
        if (Properties.TURN_OFF_UPDATES) {
            getLogger().info("Auto-updater is disabled. If you want the plugin to automatically download new releases then set 'TURN_OFF_UPDATES' to 'false' in your config.yml!");
            if (!Properties.TURN_OFF_UPDATE_NOTIFIER) {
                final Updater updater = new Updater(this, getPluginName().toLowerCase(Locale.ROOT), this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
                runInAsyncThread(() -> {
                    if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                        getLogger().info("There is a new version available: " + updater.getLatestName() + ". You can download it from https://modrinth.com/plugin/" + getPluginName().toLowerCase(Locale.ROOT));
                    }
                });
            }
            return;
        }

        new Updater(this, getPluginName().toLowerCase(Locale.ROOT), this.getFile(), Updater.UpdateType.DEFAULT, true);
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

    public static Logger getShopLogger() {
        return shopLogger;
    }

    public static Logger getBukkitLogger() {
        return logger;
    }

    public static void logDebug(String message) {
        if (Properties.DEBUG) {
            getBukkitLogger().info("[DEBUG] " + message);
        }
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

    public static Metrics getMetrics() {
        return bStats;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }

    public static void registerListener(Listener listener) {
        plugin.registerEvent(listener);
    }

    public static <E extends Event> E callEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static void sendBungeeMessage(String playerName, Messages.Message message, Map<String, String> replacementMap, String... replacements) {
        sendBungeeMessage(playerName, message.getComponent(null, true, replacementMap, replacements));
    }

    public static void sendBungeeMessage(String playerName, String message) {
        sendBungeeMessage(playerName, "Message", message);
    }

    public static void sendBungeeMessage(String playerName, BaseComponent[] message) {
        sendBungeeMessage(playerName, "MessageRaw", ComponentSerializer.toString(message));
    }

    public static void sendBungeeMessage(String playerName, Component message) {
        sendBungeeMessage(playerName, "MessageRaw", GsonComponentSerializer.gson().serialize(message));
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

    public static void runInAsyncThread(Runnable runnable) {
        executorService.submit(runnable);
    }
}
