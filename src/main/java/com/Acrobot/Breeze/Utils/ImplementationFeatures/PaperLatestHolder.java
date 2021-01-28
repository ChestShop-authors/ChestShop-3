package com.Acrobot.Breeze.Utils.ImplementationFeatures;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.BiFunction;

public class PaperLatestHolder {

    public static final BiFunction<Inventory, Boolean, InventoryHolder> PROVIDER = Inventory::getHolder;

}
