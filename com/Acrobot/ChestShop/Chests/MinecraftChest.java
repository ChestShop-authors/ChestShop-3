package com.Acrobot.ChestShop.Chests;

import com.Acrobot.ChestShop.Utils.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class MinecraftChest implements ChestObject{
    Chest main;
    Chest neighbor;
    
    public MinecraftChest(Chest chest){
        this.main = chest;
        this.neighbor = getNeighbor();
    }
    
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[(neighbor != null ? 54 : 27)];
        ItemStack[] chest1 = main.getInventory().getContents();
        
        System.arraycopy(chest1, 0, contents, 0, chest1.length);

        if(neighbor != null){
            ItemStack[] chest2 = neighbor.getInventory().getContents();
            System.arraycopy(chest2, 0, contents, chest1.length, chest2.length);
        }
        
        return contents;
    }

    public void setSlot(int slot, ItemStack item) {
        if(slot < main.getInventory().getSize()){
            main.getInventory().setItem(slot, item);
        } else{
            neighbor.getInventory().setItem(slot - main.getInventory().getSize(), item);
        }
    }

    public void clearSlot(int slot) {
        if(slot < main.getInventory().getSize()){
            main.getInventory().setItem(slot, null);
        } else{
            neighbor.getInventory().setItem(slot - main.getInventory().getSize(), null);
        }
    }

    public void addItem(ItemStack item, short durability, int amount) {
        int left = addItem(item, durability, amount, main);
        if(neighbor != null){
            addItem(item, durability, left, neighbor);
        }
    }

    public void removeItem(ItemStack item, short durability, int amount) {
        int left = removeItem(item, durability, amount, main);
        if(neighbor != null){
            removeItem(item, durability, left, neighbor);
        }
    }

    public int amount(ItemStack item, short durability) {
        return amount(item, durability, main) + (neighbor != null ? amount(item, durability, neighbor) : 0);
    }

    public boolean hasEnough(ItemStack item, int amount, short durability) {
        return amount(item, durability) >= amount;
    }

    public boolean fits(ItemStack item, int amount, short durability) {
        int firstChest = fits(item, amount, durability, main);
        return (firstChest > 0 && neighbor != null ? fits(item, amount, durability, neighbor) <= 0 : firstChest <= 0);
    }

    public int getSize() {
        return main.getInventory().getSize() + (neighbor != null ? neighbor.getInventory().getSize() : 0);
    }

    private Chest getNeighbor(){
        BlockFace[] bf = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
        Block chestBlock = main.getBlock();
        for(BlockFace blockFace : bf){
            Block neighborBlock = chestBlock.getFace(blockFace);
            if(neighborBlock.getType() == Material.CHEST){
                return (Chest) neighborBlock.getState();
            }
        }
        return null; //Shame, we didn't find double chest :/
    }

    private static int amount(ItemStack item, short durability, Chest chest){
        return InventoryUtil.amount(chest.getInventory(), item, durability);
    }

    private static int fits(ItemStack item, int amount, short durability, Chest chest){
        Inventory inv = chest.getInventory();
        return InventoryUtil.fits(inv, item, amount, durability);
    }

    private static int addItem(ItemStack item, short durability, int amount, Chest chest){
        Inventory inv = chest.getInventory();
        return InventoryUtil.add(inv, item, amount);
    }

    private static int removeItem(ItemStack item, short durability, int amount, Chest chest){
        Inventory inv = chest.getInventory();
        return InventoryUtil.remove(inv, item, amount, durability);
    }
}
