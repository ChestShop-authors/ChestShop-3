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
        itemName = itemName.replace(" ", "_");
        Material finalMaterial = Material.getMaterial(itemName.toUpperCase());
        if (finalMaterial != null) return finalMaterial;

        int length = 256;
        itemName = itemName.toLowerCase().replace("_", "");
        for (Material currentMaterial : Material.values()) {
            String materialName = currentMaterial.name().toLowerCase().replace("_", "");
            if (materialName.startsWith(itemName) && (materialName.length() < length)) {
                length = materialName.length();
                finalMaterial = currentMaterial;
            }
        }
        return finalMaterial;
    }

    public static ItemStack getItemStack(String itemName) {
        ItemStack toReturn = getFromOddItem(itemName);
        if (toReturn != null) return toReturn;

        Material material = getMaterial(itemName);
        if (material != null) return new ItemStack(material, 1);

        return getItemStackWithDataValue(itemName);
    }

    private static ItemStack getFromOddItem(String itemName) {
        return !Odd.isInitialized() ? null : Odd.returnItemStack(itemName.replace(":", ";"));
    }

    private static ItemStack getItemStackWithDataValue(String itemName) {
        if (!itemName.contains(":")) return getItemStackWithDataValueFromWord(itemName);

        String[] word = itemName.split(":");
        if (word.length < 2 || !uNumber.isInteger(word[1])) return null;

        Material item = getMaterial(word[0]);
        return item == null ? null : new ItemStack(item, 1, Short.parseShort(word[1]));
    }

    private static ItemStack getItemStackWithDataValueFromWord(String itemName) {
        int indexOfChar = itemName.indexOf(' ');

        if (indexOfChar == -1) return null;
        Material item = getMaterial(itemName.substring(indexOfChar));
        return item == null ? null : new ItemStack(item, 1, DataValue.get(itemName.substring(0, indexOfChar), item));
    }

}
