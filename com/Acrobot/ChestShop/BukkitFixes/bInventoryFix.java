package com.Acrobot.ChestShop.BukkitFixes;

import com.Acrobot.ChestShop.Chests.MinecraftChest;
import net.minecraft.server.TileEntityChest;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.inventory.Inventory;

/**
 * Temporary class until this is fixed in Bukkit
 * @author Acrobot
 */
public class bInventoryFix {
    public static Inventory getInventory(Chest chest) {
        MinecraftChest mchest = new MinecraftChest(chest);

        TileEntityChest teChest = (TileEntityChest) ((CraftWorld) chest.getWorld()).getTileEntityAt(chest.getX(), chest.getY(), chest.getZ());
        CraftInventory ci = new CraftInventory(teChest);
        
        if (mchest.getNeighbor() != null) {
            Chest nb = mchest.getNeighbor();
            TileEntityChest neighbor = (TileEntityChest) ((CraftWorld) chest.getWorld()).getTileEntityAt(nb.getX(), nb.getY(), nb.getZ());
            return new CraftInventoryDoubleChest(ci, new CraftInventory(neighbor));
        }

        return ci;
    }
}
