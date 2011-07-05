package com.Acrobot.ChestShop.Items;

import com.Acrobot.ChestShop.Utils.uNumber;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 *         Manages ItemStack names and ID's
 */
public class Items {

    public static Material getMat(String itemName) {
        if (uNumber.isInteger(itemName)) {
            return Material.getMaterial(Integer.parseInt(itemName));
        }
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
        if (Odd.isInitialized()) {
            ItemStack odd = Odd.returnItemStack(itemName.replace(":", ";"));
            if (odd != null) {
                return odd;
            }
        }
        String[] split = itemName.split(":");
        itemName = split[0];
        short dataValue = (short) (split.length > 1 && uNumber.isInteger(split[1]) ? Integer.parseInt(split[1]) : 0);

        Material mat;

        String[] data = itemName.split(" ");
        if (data.length >= 2) {
            mat = getMat(itemName.substring(itemName.indexOf(' ')));
            byte argData = DataValue.get(data[0], mat);

            if (argData != 0) {
                dataValue = argData;
            }
        } else {
            mat = getMat(itemName);
        }

        return (mat != null ? new ItemStack(mat, 1, dataValue) : null);
    }
}
