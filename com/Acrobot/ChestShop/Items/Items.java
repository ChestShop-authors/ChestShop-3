package com.Acrobot.ChestShop.Items;

import com.Acrobot.ChestShop.Utils.uNumber;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 *         Manages ItemStack names and ID's
 */
public class Items {

    public static Material getMaterial(String itemName) {
        if (uNumber.isInteger(itemName)) return Material.getMaterial(Integer.parseInt(itemName));
    
        int length = 256;
        Material finalMat = null;
        itemName = itemName.toLowerCase().replace("_", "").replace(" ", "");
        for (Material m : Material.values()) {
            String matName = m.name().toLowerCase().replace("_", "").replace(" ", "");
            if (matName.startsWith(itemName) && (matName.length() < length)) {
                length = matName.length();
                finalMat = m;
            }
        }
        return finalMat;
    }

    public static ItemStack getItemStack(String itemName) {
        ItemStack toReturn;
        if ((toReturn = getFromOddItem(itemName)) != null) {
            return toReturn;
        }

        Material material = getMaterial(itemName);
        if(material != null) return new ItemStack(material, 1);

        return getItemStackWithDataValue(itemName);
    }

    private static ItemStack getFromOddItem(String itemName) {
        if (!Odd.isInitialized()) return null;
        return Odd.returnItemStack(itemName.replace(":", ";"));
    }

    private static ItemStack getItemStackWithDataValue(String itemName){
        if(!itemName.contains(":")) return getItemStackWithDataValueFromWord(itemName);

        String[] word = itemName.split(":");
        if(word.length < 2 || !uNumber.isInteger(word[1])) return null;

        Material item = getMaterial(word[0]);
        if(item == null) return null;
        
        short dataValue = Short.parseShort(word[1]);
        return new ItemStack(item, 1, dataValue);
    }

    private static ItemStack getItemStackWithDataValueFromWord(String itemName) {
        if (!itemName.contains(" ") || getMaterial(itemName) != null) return null;
        String[] word = itemName.split(" ");
        if(word.length < 2) return null;

        String dataValue = word[0];

        String material[] = new String[word.length - 1];
        System.arraycopy(word, 1, material, 0, word.length - 1);
        StringBuilder mat = new StringBuilder();

        for(String s : material){
            mat.append(s);
        }

        Material item = getMaterial(mat.toString());

        return item == null ? null : new ItemStack(item, 1, DataValue.get(dataValue, item));


    }

}
