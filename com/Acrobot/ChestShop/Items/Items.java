package com.Acrobot.ChestShop.Items;

import com.Acrobot.ChestShop.Utils.uEnchantment;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
    
    public static String getName(ItemStack is){
        return getName(is, true);
    }
    
    public static String getName(ItemStack is, boolean showData){
        String name = DataValue.getName(is);
        return uSign.capitalizeFirst((name != null && showData ? name + '_' : "") + is.getType());
    }

    public static ItemStack getItemStack(String itemName) {
        ItemStack toReturn = getFromOddItem(itemName);
        if (toReturn != null) return toReturn;
        
        String[] split = itemName.split(":|-");

        String[] space = split[0].split(" ");
        Material material = getMaterial(split[0]);

        for (int i = (space.length > 1 ? 1 : 0); i >= 0 && material == null; i--) material = getMaterial(space[i]);

        if (material == null) return null;
        
        toReturn = new ItemStack(material, 1);
        
        for (int i = 1; i < split.length; i++){
            split[i] = split[i].trim();
            if (uNumber.isInteger(split[i])) toReturn.setDurability((short) Integer.parseInt(split[i]));
            else {
                try{ toReturn.addEnchantments(getEnchantment(split[i]));
                } catch (Exception ignored){}
            }
        }
        short data = getDataFromWord(space[0], material);
        if (data != 0) toReturn.setDurability(data);

        return toReturn;
    }
    
    private static Map<Enchantment, Integer> getEnchantment(String itemName){
        return uEnchantment.decodeEnchantment(itemName);
    }

    private static ItemStack getFromOddItem(String itemName) {
        return !Odd.isInitialized() ? null : Odd.returnItemStack(itemName.replace(":", ";"));
    }

    private static short getDataFromWord(String name, Material material) {
        return DataValue.get(name, material);
    }

}
