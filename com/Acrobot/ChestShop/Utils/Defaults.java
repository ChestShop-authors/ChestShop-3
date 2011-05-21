package com.Acrobot.ChestShop.Utils;

/**
 * @author Acrobot
 */
public class Defaults {

    public static void set(){
        Config.defaultValues.put("reverse_buttons", "false    #If true, people buy with left click and sell with right click");
        Config.defaultValues.put("shopEconomyAccount", "\"\"    #Place economy account you want Admin Shops to be assigned to");

        //LANGUAGE:
        Config.defaultValues.put("prefix", "\"&2[Shop]&f\"");
        Config.defaultValues.put("NO_BUYING_HERE", "\"You can't buy here!\"");
        Config.defaultValues.put("NO_SELLING_HERE", "\"You can't sell here!\"");
        Config.defaultValues.put("NOT_ENOUGH_SPACE_IN_INVENTORY", "\"You haven't got enough space in inventory!\"");
        Config.defaultValues.put("NOT_ENOUGH_STOCK", "\"This shop has not enough stock.\"");
        Config.defaultValues.put("NOT_ENOUGH_STOCK_IN_YOUR_SHOP", "\"Your %material shop is out of stock!\"");

        Config.defaultValues.put("YOU_BOUGHT_FROM_SHOP", "\"You bought %amount %item from %owner for %price.\"");
        Config.defaultValues.put("SOMEBODY_BOUGHT_FROM_YOUR_SHOP", "\"%buyer bought %amount %item for %price from you.\"");

        Config.defaultValues.put("YOU_SOLD_TO_SHOP", "\"You sold %amount %item to %buyer for %price.\"");
        Config.defaultValues.put("SOMEBODY_SOLD_TO_YOUR_SHOP", "\"%seller sold %amount %item for %price to you.\"");
    }
}
