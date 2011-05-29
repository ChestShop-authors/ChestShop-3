package com.Acrobot.ChestShop.Utils;

/**
 * @author Acrobot
 */
public class Defaults {

    public static void set() {
        Config.defaultValues.put("reverse_buttons", "false    #If true, people buy with left click and sell with right click");
        Config.defaultValues.put("shopEconomyAccount", "\"\"    #Economy account's name you want Admin Shops to be assigned to");
        Config.defaultValues.put("logToFile", "false    #If true, plugin will log transactions in its own file");
        Config.defaultValues.put("useDB", "false    #If true, plugin will log transactions in EBean database");

        //LANGUAGE:
        Config.defaultValues.put("prefix", "\"&a[Shop] &f\"");
        Config.defaultValues.put("iteminfo", "\"&aItem Information:&f\"");

        Config.defaultValues.put("options", "\"&aCustomizable options: \"");

        Config.defaultValues.put("NO_BUYING_HERE", "\"You can't buy here!\"");
        Config.defaultValues.put("NO_SELLING_HERE", "\"You can't sell here!\"");

        Config.defaultValues.put("NOT_ENOUGH_SPACE_IN_INVENTORY", "\"You haven't got enough space in inventory!\"");
        Config.defaultValues.put("NOT_ENOUGH_SPACE_IN_CHEST", "\"There isn't enough space in chest!\"");
        Config.defaultValues.put("NOT_ENOUGH_ITEMS_TO_SELL", "\"You have got not enough items to sell!\"");

        Config.defaultValues.put("NOT_ENOUGH_STOCK", "\"This shop has not enough stock.\"");
        Config.defaultValues.put("NOT_ENOUGH_STOCK_IN_YOUR_SHOP", "\"Your %material shop is out of stock!\"");

        Config.defaultValues.put("YOU_BOUGHT_FROM_SHOP", "\"You bought %amount %item from %owner for %price.\"");
        Config.defaultValues.put("SOMEBODY_BOUGHT_FROM_YOUR_SHOP", "\"%buyer bought %amount %item for %price from you.\"");

        Config.defaultValues.put("YOU_SOLD_TO_SHOP", "\"You sold %amount %item to %buyer for %price.\"");
        Config.defaultValues.put("SOMEBODY_SOLD_TO_YOUR_SHOP", "\"%seller sold %amount %item for %price to you.\"");

        Config.defaultValues.put("YOU_CAN'T_CREATE_SHOP", "\"You can't create this type of shop!\"");
        Config.defaultValues.put("NO_CHEST_DETECTED", "\"Couldn't find a chest!\"");
        Config.defaultValues.put("ANOTHER_SHOP_DETECTED", "\"Another player's shop detected!\"");

        Config.defaultValues.put("PROTECTED_SHOP", "\"Successfully protected the shop!\"");
        Config.defaultValues.put("SHOP_CREATED", "\"Shop successfully created!\"");

        Config.defaultValues.put("INCORRECT_ITEM_ID", "\"You have specified invalid item id!\"");
    }
}
