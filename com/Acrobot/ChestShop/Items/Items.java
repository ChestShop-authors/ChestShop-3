package com.Acrobot.ChestShop.Items;

import com.Acrobot.ChestShop.Utils.Numerical;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 * Manages ItemStack names and ID's
 */
public class Items {
    
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

    public static ItemStack getItemStack(String itemName){
        if(Odd.isInitialized()){
            ItemStack odd = Odd.returnItemStack(itemName.replace(":", ";"));
            if(odd != null){
                return odd;
            }
        }
        String[] split = itemName.split(":");
        itemName = split[0];
        short dataValue = (short) (split.length > 1 && Numerical.isInteger(split[1]) ? Integer.parseInt(split[1]) : 0);

        if(Numerical.isInteger(itemName)){
            return new ItemStack(Material.getMaterial(Integer.parseInt(itemName)), 1, dataValue);
        }

        Material mat = getMaterial(itemName);

        return (mat != null ? new ItemStack(mat, 1, dataValue) : null);
    }
}
