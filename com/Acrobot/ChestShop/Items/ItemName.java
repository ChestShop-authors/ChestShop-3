package com.Acrobot.ChestShop.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 * Manages ItemStack names and ID's
 */
public class ItemName {
    
    public static String getItemName(ItemStack itemStack){
        return getItemName(itemStack.getType().name());
    }

    public static String getItemName(String itemName){
        return getMaterial(itemName).name();
    }

    public static Material getMaterial(String itemName){
        int length = 256;
        Material finalMat = null;
        itemName = itemName.toLowerCase().replace("_","").replace(" ", "");
        for(Material m: Material.values()){
            String matName = m.name().toLowerCase().replace("_","").replace(" ", "");
            if(matName.startsWith(itemName) && (matName.length() < length)){
                length = matName.length();
                finalMat = m;
            }
        }
        return finalMat;
    }

    public static int getItemID(String itemName){
        return getMaterial(itemName).getId();
    }
}
