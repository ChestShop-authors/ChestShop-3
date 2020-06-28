package com.Acrobot.ChestShop.Configuration;

import com.Acrobot.Breeze.Configuration.Annotations.ConfigurationComment;
import com.Acrobot.Breeze.Configuration.Annotations.Parser;
import com.Acrobot.Breeze.Configuration.Annotations.PrecededBySpace;
import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.Breeze.Configuration.ValueParser;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Security;
import org.bukkit.Material;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Acrobot
 */
public class Properties {

    static {
        Configuration.registerParser("StringSet", new ValueParser() {
            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                if (object instanceof Collection) {
                    return new LinkedHashSet<>((Collection<String>) object);
                }
                return object;
            }
        });
        Configuration.registerParser("MaterialSet", new ValueParser() {
            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                if (object instanceof Collection) {
                    EnumSet<Material> set = EnumSet.noneOf(Material.class);
                    for (Object o : (Collection) object) {
                        if (o instanceof Material) {
                            set.add((Material) o);
                        } else if (o instanceof String) {
                            try {
                                set.add(Material.getMaterial(((String) o).toUpperCase(Locale.ROOT)));
                            } catch (IllegalArgumentException e) {
                                ChestShop.getBukkitLogger().log(Level.WARNING, o + " is not a valid Material name in the config!");
                            }
                        }
                    }
                    return set;
                }
                return object;
            }
        });
        Configuration.registerParser("BigDecimal", new ValueParser() {
            @Override
            public String parseToYAML(Object object) {
                if (object instanceof BigDecimal) {
                    return object.toString();
                }
                return super.parseToYAML(object);
            }

            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                if (object instanceof Double) {
                    return BigDecimal.valueOf((Double) object);
                } else if (object instanceof Long) {
                    return BigDecimal.valueOf((Long) object);
                } else if (object instanceof Integer) {
                    return BigDecimal.valueOf((Integer) object);
                }
                return object;
            }
        });
        Configuration.registerParser("UUID", new ValueParser() {
            @Override
            public String parseToYAML(Object object) {
                if (object instanceof UUID) {
                    return object.toString();
                }
                return super.parseToYAML(object);
            }

            @Override
            public <T> Object parseToJava(Class<T> type, Object object) {
                if (object instanceof String) {
                    return UUID.fromString((String) object);
                }
                return object;
            }
        });
    }

    @ConfigurationComment("Should the plugin log some messages that are useful for debugging?")
    public static boolean DEBUG = false;

    @PrecededBySpace
    @ConfigurationComment("Do you want to turn off the automatic updates of ChestShop?")
    public static boolean TURN_OFF_UPDATES = true;

    @ConfigurationComment("Do you want to turn off the automatic notifications for new development builds?")
    public static boolean TURN_OFF_DEV_UPDATE_NOTIFIER = false;

    @ConfigurationComment("Do you want to include some values of this config in the metrics? (This will not leak sensitive data but help in the development process)")
    public static boolean INCLUDE_SETTINGS_IN_METRICS = true;

    @PrecededBySpace
    @ConfigurationComment("How large should the internal caches be?")
    public static int CACHE_SIZE = 1000;

    @PrecededBySpace
    @ConfigurationComment("The default language when the client's language can't be found.")
    public static String DEFAULT_LANGUAGE = "en";

    @ConfigurationComment("Should the plugin try to use a language file that matches the client's locale setting?")
    public static boolean USE_CLIENT_LOCALE = true;

    @PrecededBySpace
    @ConfigurationComment("What containers are allowed to hold a shop? (Only blocks with inventories work!)")
    @Parser("MaterialSet")
    public static Set<Material> SHOP_CONTAINERS = EnumSet.of(
            Material.CHEST,
            Material.TRAPPED_CHEST
    );

    @PrecededBySpace
    @ConfigurationComment("(In 1/1000th of a second) How often can a player use the shop sign?")
    public static int SHOP_INTERACTION_INTERVAL = 250;

    @ConfigurationComment("Do you want to allow using shops to people in creative mode?")
    public static boolean IGNORE_CREATIVE_MODE = true;

    @ConfigurationComment("Do you want to allow using shops to people who have access to it due to their permissions? (owners are always ignored)")
    public static boolean IGNORE_ACCESS_PERMS = true;

    @ConfigurationComment("If true, people will buy with left-click and sell with right-click.")
    public static boolean REVERSE_BUTTONS = false;

    @ConfigurationComment("If true, people will be able to buy/sell in 64 stacks while holding the crouch button.")
    public static boolean SHIFT_SELLS_IN_STACKS = false;

    @ConfigurationComment("If true, people will be able to sell/buy everything available of the same type.")
    public static boolean SHIFT_SELLS_EVERYTHING = false;

    @ConfigurationComment("What can you do by clicking shift with SHIFT_SELLS_IN_STACKS turned on? (ALL/BUY/SELL)")
    public static String SHIFT_ALLOWS = "ALL";

    @ConfigurationComment("Can shop's chest be opened by owner with right-clicking a shop's sign?")
    public static boolean ALLOW_SIGN_CHEST_OPEN = true;

    @ConfigurationComment("If true, when you left-click your own shop sign you won't open chest's inventory, but instead you will start destroying the sign.")
    public static boolean ALLOW_LEFT_CLICK_DESTROYING = true;

    @PrecededBySpace
    @ConfigurationComment("If true, if the shop is empty, the sign is destroyed and put into the chest, so the shop isn't usable anymore.")
    public static boolean REMOVE_EMPTY_SHOPS = false;

    @ConfigurationComment("If true, if the REMOVE_EMPTY_SHOPS option is turned on, the chest is also destroyed.")
    public static boolean REMOVE_EMPTY_CHESTS = false;

    @ConfigurationComment("A list of worlds in which to remove empty shops with the previous config. Case sensitive. An empty list means all worlds.")
    @Parser("StringSet")
    public static Set<String> REMOVE_EMPTY_WORLDS = new LinkedHashSet<>(Arrays.asList("world1", "world2"));

    @PrecededBySpace
    @ConfigurationComment("First line of your Admin Shop's sign should look like this:")
    public static String ADMIN_SHOP_NAME = "Admin Shop";

    @ConfigurationComment("The name of the economy account which Admin Shops should use and to which all taxes will go")
    public static String SERVER_ECONOMY_ACCOUNT = "";

    @ConfigurationComment("The uuid of the economy account for the Admin Shop. Useful for fake accounts as normally only accounts of players work")
    public static UUID SERVER_ECONOMY_ACCOUNT_UUID = new UUID(0, 0);

    @ConfigurationComment("Percent of the price that should go to the server's account. (100 = 100 percent)")
    public static int TAX_AMOUNT = 0;

    @ConfigurationComment("Percent of the price that should go to the server's account when buying from an Admin Shop.")
    public static int SERVER_TAX_AMOUNT = 0;

    @ConfigurationComment("Amount of money player must pay to create a shop")
    public static BigDecimal SHOP_CREATION_PRICE = BigDecimal.valueOf(0);

    @ConfigurationComment("How much money do you get back when destroying a sign?")
    public static BigDecimal SHOP_REFUND_PRICE = BigDecimal.valueOf(0);

    @ConfigurationComment("How many decimal places are allowed at a maximum for prices?")
    public static int PRICE_PRECISION = 2;

    @PrecededBySpace
    @ConfigurationComment("Should we block shops that sell things for more than they buy? (This prevents newbies from creating shops that would be exploited)")
    public static boolean BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE = true;

    @PrecededBySpace
    @ConfigurationComment("Maximum amount of items that can be bought/sold at a shop. Default 3456 is a double chest of 64 stacks.")
    public static int MAX_SHOP_AMOUNT = 3456;

    @PrecededBySpace
    @ConfigurationComment("Do you want to allow other players to build a shop on a block where there's one already?")
    public static boolean ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK = false;

    @ConfigurationComment("Can shops be used even when the buyer/seller doesn't have enough items, space or money? (The price will be scaled adequately to the item amount)")
    public static boolean ALLOW_PARTIAL_TRANSACTIONS = true;

    @ConfigurationComment("Can '?' be put in place of item name in order for the sign to be auto-filled?")
    public static boolean ALLOW_AUTO_ITEM_FILL = true;

    @PrecededBySpace
    @ConfigurationComment("Enable this if you use BungeeCord and want players to receive shop notifications on other servers")
    public static boolean BUNGEECORD_MESSAGES = false;

    @PrecededBySpace
    @ConfigurationComment("Do you want to show \"Out of stock\" messages?")
    public static boolean SHOW_MESSAGE_OUT_OF_STOCK = true;
    @ConfigurationComment("Do you want to show \"Full shop\" messages?")
    public static boolean SHOW_MESSAGE_FULL_SHOP = true;
    @ConfigurationComment("How many seconds do you want to wait before showing notifications for the same shop to the owner again?")
    public static long NOTIFICATION_MESSAGE_COOLDOWN = 10;

    @PrecededBySpace
    @ConfigurationComment("Can players hide the \"Out of stock\" messages with /cstoggle?")
    public static boolean CSTOGGLE_TOGGLES_OUT_OF_STOCK = false;
    @ConfigurationComment("Can players hide the \"Full shop\" messages with /cstoggle?")
    public static boolean CSTOGGLE_TOGGLES_FULL_SHOP = false;

    @ConfigurationComment("Do you want to show \"You bought/sold... \" messages?")
    public static boolean SHOW_TRANSACTION_INFORMATION_CLIENT = true;

    @ConfigurationComment("Do you want to show \"Somebody bought/sold... \" messages?")
    public static boolean SHOW_TRANSACTION_INFORMATION_OWNER = true;

    @PrecededBySpace
    @ConfigurationComment("If true, plugin will log transactions in its own file")
    public static boolean LOG_TO_FILE = false;

    @ConfigurationComment("Do you want ChestShop's messages to show up in console?")
    public static boolean LOG_TO_CONSOLE = true;

    @ConfigurationComment("Should all shop removals be logged to the console?")
    public static boolean LOG_ALL_SHOP_REMOVALS = true;

    @PrecededBySpace
    @ConfigurationComment("Do you want to stack all items up to 64 item stacks?")
    public static boolean STACK_TO_64 = false;

    @ConfigurationComment("Do you want to use built-in protection against chest destruction?")
    public static boolean USE_BUILT_IN_PROTECTION = true;

    @ConfigurationComment("Do you want to have shop signs \"stick\" to chests?")
    public static boolean STICK_SIGNS_TO_CHESTS = false;

    @ConfigurationComment("EXPERIMENTAL: Do you want to turn off the default protection when another plugin is protecting the block? (Will leave the chest visually open - CraftBukkit bug!)")
    public static boolean TURN_OFF_DEFAULT_PROTECTION_WHEN_PROTECTED_EXTERNALLY = false;

    @ConfigurationComment("Do you want to turn off the default sign protection? Warning! Other players will be able to destroy other people's shops!")
    public static boolean TURN_OFF_SIGN_PROTECTION = false;

    @ConfigurationComment("Do you want to disable the hopper protection, which prevents Hopper-Minecarts from taking items out of shops?")
    public static boolean TURN_OFF_HOPPER_PROTECTION = false;

    @ConfigurationComment("Only allow users to buy/sell that have access to the sign's protection? (E.g. LWC protection)")
    public static boolean CHECK_ACCESS_FOR_SHOP_USE = false;

    @ConfigurationComment("Do you want to protect shop chests with LWC?")
    public static boolean PROTECT_CHEST_WITH_LWC = false;

    @ConfigurationComment("Of which type should the container protection be? Possible type: public, private, donate and on some LWC versions display")
    public static Security.Type LWC_CHEST_PROTECTION_TYPE = Security.Type.PRIVATE;

    @ConfigurationComment("Do you want to protect shop signs with LWC?")
    public static boolean PROTECT_SIGN_WITH_LWC = false;

    @ConfigurationComment("Of which type should the sign protection be? Possible type: public, private, donate and on some LWC versions display")
    public static Security.Type LWC_SIGN_PROTECTION_TYPE = Security.Type.PRIVATE;

    @ConfigurationComment("Should the chest's LWC protection be removed once the shop sign is destroyed? ")
    public static boolean REMOVE_LWC_PROTECTION_AUTOMATICALLY = true;

    @ConfigurationComment("Should LWC limits block shop creations?")
    public static boolean LWC_LIMITS_BLOCK_CREATION = true;

    @PrecededBySpace
    @ConfigurationComment("Do you want to only let people build inside WorldGuard regions?")
    public static boolean WORLDGUARD_INTEGRATION = false;

    @ConfigurationComment("Do you want to only let people build inside region flagged by doing /region regionName flag allow-shop allow?")
    public static boolean WORLDGUARD_USE_FLAG = false;

    @ConfigurationComment("Do you want ChestShop to respect WorldGuard's chest protection?")
    public static boolean WORLDGUARD_USE_PROTECTION = false;

    @PrecededBySpace
    @ConfigurationComment("Do you want to only let people build inside GriefPrevention claims?")
    public static boolean GRIEFPREVENTION_INTEGRATION = false;

    @PrecededBySpace
    @ConfigurationComment("Do you want to only let people build inside RedProtect regions?")
    public static boolean REDPROTECT_INTEGRATION = false;

    @PrecededBySpace
    @ConfigurationComment("Do you want to deny shop access to unlogged users?")
    public static boolean AUTHME_HOOK = true;

    @ConfigurationComment("Do you want to allow shop access to unregistered users? (Example: registration is optional)")
    public static boolean AUTHME_ALLOW_UNREGISTERED = false;

    @PrecededBySpace
    @ConfigurationComment("How much Heroes exp should people get for creating a ChestShop?")
    public static double HEROES_EXP = 100;

    @PrecededBySpace
    @ConfigurationComment("Add icons and make item names hoverable in transaction messages when ShowItem is installed?")
    public static boolean SHOWITEM_MESSAGE = true;
}