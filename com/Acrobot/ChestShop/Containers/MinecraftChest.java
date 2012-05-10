package com.Acrobot.ChestShop.Containers;

import com.Acrobot.ChestShop.Utils.uBlock;
import com.Acrobot.ChestShop.Utils.uInventory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class MinecraftChest implements Container {
    private final Chest chest;
    private final BlockFace[] neighborFaces = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public MinecraftChest(Chest chest) {
        this.chest = chest;
    }

    public boolean isEmpty() {
        for (ItemStack item : chest.getInventory()) {
            if (item != null) {
                return false;
            }
        }

        return true;
    }

    public void addItem(ItemStack item, int amount) {
        uInventory.add(chest.getInventory(), item, amount);
    }

    public void removeItem(ItemStack item, short durability, int amount) {
        uInventory.remove(chest.getInventory(), item, amount, durability);
    }

    public int amount(ItemStack item, short durability) {
        return uInventory.amount(chest.getInventory(), item, durability);
    }

    public boolean hasEnough(ItemStack item, int amount, short durability) {
        return amount(item, durability) >= amount;
    }

    public boolean fits(ItemStack item, int amount, short durability) {
        return uInventory.fits(chest.getInventory(), item, amount, durability) <= 0;
    }

    public Sign findShopSign() {
        Sign sign = uBlock.findAnyNearbyShopSign(chest.getBlock());

        if (sign == null && getNeighbor() != null) {
            sign = uBlock.findAnyNearbyShopSign(getNeighbor().getBlock());
        }

        return sign;
    }

    private Chest getNeighbor() {
        Block chestBlock = chest.getBlock();

        for (BlockFace chestFace : neighborFaces) {
            Block relative = chestBlock.getRelative(chestFace);

            if (relative.getState() instanceof Chest) {
                return (Chest) relative.getState();
            }
        }

        return null;
    }
}
