package com.Acrobot.iConomyChestShop.Chests;

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
        return fits(item, amount, durability, main) && (neighbor == null || fits(item, amount, durability, neighbor));
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

    private int amount(ItemStack item, short durability, Chest chest){
        ItemStack[] contents = chest.getInventory().getContents();
        int amount = 0;

        for(ItemStack i : contents){
            if(i != null){
                if(i.getType() == item.getType() && (durability == -1 || i.getDurability() == durability || (durability == 0 && i.getDurability() == -1))){
                    amount += i.getAmount();
                }
            }
        }
        return amount;
    }

    private boolean fits(ItemStack item, int amount, short durability, Chest chest){
        Inventory inv = chest.getInventory();
        Material itemMaterial = item.getType();
        int maxStackSize = item.getMaxStackSize();

        int amountLeft = amount;

        for(int slot = 0; slot < inv.getSize(); slot++){

            if(amountLeft <= 0){
                return true;
            }

            ItemStack currentItem = inv.getItem(slot);

            if(currentItem == null || currentItem.getType() == Material.AIR){
                amountLeft -= maxStackSize;
                continue;
            }

            if(currentItem.getType() == itemMaterial && (currentItem.getDurability() == durability)){
                int currentAmount = currentItem.getAmount();
                if(amountLeft == currentAmount){
                    amountLeft = 0;
                } else if(amountLeft < currentAmount){
                    amountLeft = 0;
                } else{
                    amountLeft -= currentAmount;
                }
            }
        }

        return amountLeft <= 0;
    }

    private int addItem(ItemStack item, short durability, int amount, Chest chest){
        Inventory inv = chest.getInventory();
        ItemStack[] contents = inv.getContents();
        Material itemMaterial = item.getType();

        int amountLeft = amount;
        int maxStackSize = item.getMaxStackSize();
        ItemStack baseItem = item.clone();

        for(int slot = 0; slot < inv.getSize(); slot++){
            ItemStack itemStack = contents[slot];
            if(amountLeft <= 0){
                return 0;
            }
            if(itemStack != null && itemStack.getType() != Material.AIR){ //Our slot is not free
                int currentAmount = itemStack.getAmount();
                Material currentMaterial = itemStack.getType();
                short currentDurability = itemStack.getDurability();
                if(currentMaterial == itemMaterial && (currentDurability == durability)){
                    if((currentAmount + amountLeft) <= maxStackSize){
                        baseItem.setAmount(currentAmount + amountLeft);
                        amountLeft = 0;
                    } else{
                        baseItem.setAmount(maxStackSize);
                        amountLeft -= (maxStackSize - currentAmount);
                    }
                    inv.setItem(slot, baseItem);
                }
            }else{ //Free slot
                if(amountLeft <= maxStackSize){ //There is less to add than whole stack
                    baseItem.setAmount(amountLeft);
                    inv.setItem(slot, baseItem);
                    amountLeft = 0;
                } else{ //We add whole stack
                    baseItem.setAmount(maxStackSize);
                    inv.setItem(slot, baseItem);
                    amountLeft -= maxStackSize;
                }
            }
        }
        return amountLeft;
    }

    private int removeItem(ItemStack item, short durability, int amount, Chest chest){
        Inventory inv = chest.getInventory();
        Material itemMaterial = item.getType();

        int amountLeft = amount;

        for(int slot = 0; slot < inv.getSize(); slot++){

            if(amountLeft <= 0){
                return 0;
            }
            
            ItemStack currentItem = inv.getItem(slot);

            if(currentItem == null || currentItem.getType() == Material.AIR){
                continue;
            }
            if(currentItem.getType() == itemMaterial && (currentItem.getDurability() == durability)){
                int currentAmount = currentItem.getAmount();
                if(amountLeft == currentAmount){
                    currentItem = null;
                    amountLeft = 0;
                } else if(amountLeft < currentAmount){
                    currentItem.setAmount(currentAmount - amountLeft);
                    amountLeft = 0;
                } else{
                    currentItem = null;
                    amountLeft -= currentAmount;
                }
                inv.setItem(slot, currentItem);
            }
        }

        return amountLeft;
    }
}
