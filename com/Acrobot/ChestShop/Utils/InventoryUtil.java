package com.Acrobot.ChestShop.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class InventoryUtil {

    public static int remove(Inventory inv, ItemStack item, int amount, short durability){
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
            
            if(currentItem.getType() == itemMaterial && (durability == -1 || currentItem.getDurability() == durability)){
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

    public static int add(Inventory inv, ItemStack item, int amount){
        ItemStack[] contents = inv.getContents();
        Material itemMaterial = item.getType();
        short durability = item.getDurability();

        int amountLeft = amount;
        int maxStackSize = itemMaterial.getMaxStackSize();
        ItemStack baseItem = item.clone();

        for(int slot = 0; slot++ <= inv.getSize();){
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

    public static int amount(Inventory inv, ItemStack item, short durability){
        int amount = 0;
        ItemStack[] contents = inv.getContents();
        for(ItemStack i : contents){
            if(i != null){
                if(i.getType() == item.getType() && (durability == -1 || i.getDurability() == durability || (durability == 0 && i.getDurability() == -1))){
                    amount += i.getAmount();
                }
            }
        }
        return amount;
    }

    public static int fits(Inventory inv, ItemStack item, int amount, short durability){
        Material itemMaterial = item.getType();
        int maxStackSize = itemMaterial.getMaxStackSize();
        
        int amountLeft = amount;

        for(ItemStack currentItem : inv.getContents()){
            if(amountLeft <= 0){
                return 0;
            }

            if(currentItem == null || currentItem.getType() == Material.AIR){
                amountLeft -= maxStackSize;
                continue;
            }

            int currentAmount = currentItem.getAmount();
            if(currentAmount == itemMaterial.getMaxStackSize()){
                continue;
            }
            
            if(currentItem.getType() == itemMaterial && (durability == -1 || currentItem.getDurability() == durability)){
                currentAmount = currentAmount < 1 ? 1 : currentAmount;
                amountLeft = (amountLeft <= currentAmount ? 0 : currentAmount);
            }
        }

        return amountLeft;

    }
}
