package com.Acrobot.Breeze.Utils.ImplementationFeatures;

import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.BiFunction;

public class PaperLatestHolder {

    public static final BiFunction<Inventory, Boolean, InventoryHolder> PROVIDER = Inventory::getHolder;

    public static final BiFunction<DoubleChest, Boolean, InventoryHolder> LEFT_PROVIDER = DoubleChest::getLeftSide;

    public static final BiFunction<DoubleChest, Boolean, InventoryHolder> RIGHT_PROVIDER = DoubleChest::getRightSide;

}
