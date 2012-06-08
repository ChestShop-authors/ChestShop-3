package com.Acrobot.ChestShop.Containers;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class ShopChest implements Container {
    private final Chest chest;
    private static final BlockFace[] NEIGHBOR_FACES = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public ShopChest(Chest chest) {
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

    public void addItem(ItemStack item) {
        InventoryUtil.add(item, chest.getInventory());
    }

    public void removeItem(ItemStack item) {
        InventoryUtil.remove(item, chest.getInventory());
    }

    public int amount(ItemStack item) {
        return InventoryUtil.getAmount(item, chest.getInventory());
    }

    public boolean hasEnough(ItemStack item) {
        return amount(item) >= item.getAmount();
    }

    public boolean fits(ItemStack item) {
        return InventoryUtil.fits(item, chest.getInventory());
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

        for (BlockFace chestFace : NEIGHBOR_FACES) {
            Block relative = chestBlock.getRelative(chestFace);

            if (relative.getState() instanceof Chest) {
                return (Chest) relative.getState();
            }
        }

        return null;
    }
}
