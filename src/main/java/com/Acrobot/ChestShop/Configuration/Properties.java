package com.Acrobot.ChestShop.Configuration;

import com.Acrobot.Breeze.Configuration.Annotations.ConfigurationComment;
import com.Acrobot.Breeze.Configuration.Annotations.PrecededBySpace;

/**
 * @author Acrobot
 */
public class Properties {
    @ConfigurationComment("Do you want to turn off the automatic updates of ChestShop?")
    public static boolean TURN_OFF_UPDATES = false;

    @PrecededBySpace
    @ConfigurationComment("(In 1/1000th of a second) How often can a player use the shop sign?")
    public static int SHOP_INTERACTION_INTERVAL = 250;

    @ConfigurationComment("Do you want to allow using shops to people in creative mode?")
    public static boolean IGNORE_CREATIVE_MODE = true;

    @ConfigurationComment("If true, people will buy with left-click and sell with right-click.")
    public static boolean REVERSE_BUTTONS = false;

    @ConfigurationComment("If true, people will be able to buy/sell in 64 stacks while holding the crouch button.")
    public static boolean SHIFT_SELLS_IN_STACKS = false;

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

    @PrecededBySpace
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

    @PrecededBySpace
    @ConfigurationComment("Should we block shops that sell things for more than they buy? (This prevents newbies from creating shops that would be exploited)")
    public static boolean BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE = true;

    @PrecededBySpace
    @ConfigurationComment("Do you want to allow other players to build a shop on a block where there's one already?")
    public static boolean ALLOW_MULTIPLE_SHOPS_AT_ONE_BLOCK = false;

    @ConfigurationComment("Can shops be used even when the seller doesn't have enough items? (The price will be scaled adequatly to the item amount)")
    public static boolean ALLOW_PARTIAL_TRANSACTIONS = true;

    @ConfigurationComment("Can '?' be put in place of item name in order for the sign to be auto-filled?")
    public static boolean ALLOW_AUTO_ITEM_FILL = true;

    @PrecededBySpace
    @ConfigurationComment("Do you want to show \"Out of stock\" messages?")
    public static boolean SHOW_MESSAGE_OUT_OF_STOCK = true;

    @ConfigurationComment("Do you want to show \"You bought/sold... \" messages?")
    public static boolean SHOW_TRANSACTION_INFORMATION_CLIENT = true;

    @ConfigurationComment("Do you want to show \"Somebody bought/sold... \" messages?")
    public static boolean SHOW_TRANSACTION_INFORMATION_OWNER = true;

    @PrecededBySpace
    @ConfigurationComment("If true, plugin will log transactions in its own file")
    public static boolean LOG_TO_FILE = false;

    @ConfigurationComment("Do you want ChestShop's messages to show up in console?")
    public static boolean LOG_TO_CONSOLE = true;

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

    @ConfigurationComment("Do you want to disable the hopper protection, which prevents the hoppers from taking items out of chests?")
    public static boolean TURN_OFF_HOPPER_PROTECTION = false;

    @ConfigurationComment("Do you want to protect shop chests with LWC?")
    public static boolean PROTECT_CHEST_WITH_LWC = false;

    @ConfigurationComment("Do you want to protect shop signs with LWC?")
    public static boolean PROTECT_SIGN_WITH_LWC = false;

    @ConfigurationComment("Should the chest's LWC protection be removed once the shop sign is destroyed? ")
    public static boolean REMOVE_LWC_PROTECTION_AUTOMATICALLY = true;

    @PrecededBySpace
    @ConfigurationComment("Do you want to only let people build inside regions?")
    public static boolean WORLDGUARD_INTEGRATION = false;

    @ConfigurationComment("Do you want to only let poeple build inside region flagged by doing /region regionName flag chestshop allow?")
    public static boolean WORLDGUARD_USE_FLAG = true;

    @ConfigurationComment("Do you want ChestShop to respect WorldGuard's chest protection?")
    public static boolean WORLDGUARD_USE_PROTECTION = false;

    @PrecededBySpace
    @ConfigurationComment("Do you want to deny shop access to unlogged users?")
    public static boolean AUTHME_HOOK = true;

    @ConfigurationComment("Do you want to allow shop access to unregistered users? (Example: registration is optional)")
    public static boolean AUTHME_ALLOW_UNREGISTERED = false;

    @PrecededBySpace
    @ConfigurationComment("How much Heroes exp should people get for creating a ChestShop?")
    public static double HEROES_EXP = 100;
}