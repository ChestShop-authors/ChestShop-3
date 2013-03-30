package com.Acrobot.ChestShop.Configuration;

import com.Acrobot.Breeze.Configuration.ConfigurationComment;

/**
 * @author Acrobot
 */
public class Properties {
    @ConfigurationComment("(When not using Vault) Preferred economy plugin to use (iConomy/BOSEconomy/Essentials).")
    public static String PREFERRED_ECONOMY_PLUGIN = "";

    public static byte NEWLINE_PREFERRED_ECONOMY_PLUGIN; ///////////////////////////////////////////////////

    @ConfigurationComment("(In 1/1000th of a second) How often can a player use the shop sign?")
    public static int SHOP_INTERACTION_INTERVAL = 250;

    @ConfigurationComment("Do you want to allow using shops to people in creative mode?")
    public static boolean IGNORE_CREATIVE_MODE = true;

    @ConfigurationComment("If true, people will buy with left-click and sell with right-click.")
    public static boolean REVERSE_BUTTONS = false;

    @ConfigurationComment("If true, people will be able to sell/buy everything available of the same type.")
    public static boolean SHIFT_SELLS_EVERYTHING = false;

    @ConfigurationComment("What can you do by clicking shift with SHIFT_SELLS_EVERYTHING turned on? (ALL/BUY/SELL)")
    public static String SHIFT_ALLOWS = "ALL";

    @ConfigurationComment("Can shop's chest be opened by owner with right-clicking a shop's sign?")
    public static boolean ALLOW_SIGN_CHEST_OPEN = true;

    @ConfigurationComment("If true, when you left-click your own shop sign you won't open chest's inventory, but instead you will start destroying the sign.")
    public static boolean ALLOW_LEFT_CLICK_DESTROYING = true;

    public static byte NEWLINE_ALLOW_LEFT_CLICK_DESTROYING; ///////////////////////////////////////////////////

    @ConfigurationComment("If true, if the shop is empty, the sign is destroyed and put into the chest, so the shop isn't usable anymore.")
    public static boolean REMOVE_EMPTY_SHOPS = false;

    @ConfigurationComment("If true, if the REMOVE_EMPTY_SHOPS option is turned on, the chest is also destroyed.")
    public static boolean REMOVE_EMPTY_CHESTS = false;

    public static byte NEWLINE_REMOVE_EMPTY_CHESTS; ///////////////////////////////////////////////////

    @ConfigurationComment("First line of your Admin Shop's sign should look like this:")
    public static String ADMIN_SHOP_NAME = "Admin Shop";

    @ConfigurationComment("The economy account which Admin Shops should use and to which all taxes will go")
    public static String SERVER_ECONOMY_ACCOUNT = "";

    @ConfigurationComment("Percent of the price that should go to the server's account. (100 = 100 percent)")
    public static int TAX_AMOUNT = 0;

    @ConfigurationComment("Percent of the price that should go to the server's account when buying from an Admin Shop.")
    public static int SERVER_TAX_AMOUNT = 0;

    @ConfigurationComment("Amount of money player must pay to create a shop")
    public static double SHOP_CREATION_PRICE = 0;

    @ConfigurationComment("How much money do you get back when destroying a sign?")
    public static double SHOP_REFUND_PRICE = 0;

    public static byte NEWLINE_SHOP_REFUND_PRICE; ///////////////////////////////////////////////////

    @ConfigurationComment("Should we block shops that sell things for more than they buy? (This prevents newbies from creating shops that would be exploited)")
    public static boolean BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE = true;

    public static byte NEWLINE_BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE; ///////////////////////////////////////////////////

    @ConfigurationComment("Do you want to allow other players to build a shop on a block where there's one already?")
    public static boolean ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK = false;

    @ConfigurationComment("Can shops be used even when the seller doesn't have enough items? (The price will be scaled adequatly to the item amount)")
    public static boolean ALLOW_PARTIAL_TRANSACTIONS = true;

    public static byte NEWLINE_ALLOW_PARTIAL_TRANSACTIONS; ///////////////////////////////////////////////////

    @ConfigurationComment("Do you want to show \"Out of stock\" messages?")
    public static boolean SHOW_MESSAGE_OUT_OF_STOCK = true;

    @ConfigurationComment("Do you want to show \"You bought/sold... \" messages?")
    public static boolean SHOW_TRANSACTION_INFORMATION_CLIENT = true;

    @ConfigurationComment("Do you want to show \"Somebody bought/sold... \" messages?")
    public static boolean SHOW_TRANSACTION_INFORMATION_OWNER = true;

    public static byte NEWLINE_SHOW_TRANSACTION_INFORMATION_OWNER; ///////////////////////////////////////////////////

    @ConfigurationComment("If true, plugin will log transactions in its own file")
    public static boolean LOG_TO_FILE = false;

    @ConfigurationComment("Do you want ChestShop's messages to show up in console?")
    public static boolean LOG_TO_CONSOLE = true;

    @ConfigurationComment("If true, plugin will log transactions in EBean database")
    public static boolean LOG_TO_DATABASE = false;

    @ConfigurationComment("How long should transaction information be stored in the database (in seconds, -1 means forever)?")
    public static int RECORD_TIME_TO_LIVE = 600;

    public static byte NEWLINE_RECORD_TIME_TO_LIV; ///////////////////////////////////////////////////

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

    @ConfigurationComment("Do you want to protect shop chests with LWC?")
    public static boolean PROTECT_CHEST_WITH_LWC = false;

    @ConfigurationComment("Do you want to protect shop signs with LWC?")
    public static boolean PROTECT_SIGN_WITH_LWC = false;

    public static byte NEWLINE_PROTECT_SIGN_WITH_LWC; ///////////////////////////////////////////////////

    @ConfigurationComment("If true, plugin will generate shop statistics webpage.")
    public static boolean GENERATE_STATISTICS_PAGE = false;

    @ConfigurationComment("Where should your generated website be saved?")
    public static String STATISTICS_PAGE_PATH = "plugins/ChestShop/website.html";

    @ConfigurationComment("How often should the website be generated?")
    public static long STATISTICS_PAGE_GENERATION_INTERVAL = 60;

    public static byte NEWLINE_STATISTICS_PAGE_GENERATION_INTERVAL; ///////////////////////////////////////////////////

    @ConfigurationComment("Do you want to only let people build inside shop plots?")
    public static boolean TOWNY_INTEGRATION = false;

    @ConfigurationComment("If true, only plot owners are able to build inside a shop plot. If false, every town's resident is able to build there.")
    public static boolean TOWNY_SHOPS_FOR_OWNERS_ONLY = true;

    public static byte NEWLINE_TOWNY_SHOPS_FOR_OWNERS_ONLY; ///////////////////////////////////////////////////

    @ConfigurationComment("Do you want to only let people build inside regions?")
    public static boolean WORLDGUARD_INTEGRATION = false;

    @ConfigurationComment("Do you want to only let poeple build inside region flagged by doing /region regionName flag chestshop allow?")
    public static boolean WORLDGUARD_USE_FLAG = true;

    @ConfigurationComment("Do you want ChestShop to respect WorldGuard's chest protection?")
    public static boolean WORLDGUARD_USE_PROTECTION = false;

    public static byte NEWLINE_WORLDGUARD_USE_PROTECTION; ///////////////////////////////////////////////////

    @ConfigurationComment("How much Heroes exp should people get for creating a ChestShop?")
    public static double HEROES_EXP = 100;
}
