package com.Acrobot.ChestShop.Items;

import com.Acrobot.ChestShop.Utils.uEnchantment;
import com.Acrobot.ChestShop.Utils.uNumber;
import com.Acrobot.ChestShop.Utils.uSign;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Acrobot
 *         Manages ItemStack names and ID's
 */
public class Items {
    private static final Pattern Durability = Pattern.compile(":(\\d)*");
    private static final Pattern Enchant = Pattern.compile("-([0-9a-zA-Z])*");

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
        
        String first = itemName.split(":|-")[0];

        String[] space = first.split(" ");
        Material material = getMaterial(first);

        for (int i = (space.length > 1 ? 1 : 0); i >= 0 && material == null; i--) material = getMaterial(space[i]);

        if (material == null) return null;
        
        toReturn = new ItemStack(material, 1);
        toReturn = addEnchantments(toReturn, itemName);
        toReturn = addDurability(toReturn, itemName);
        
        short data = getDataFromWord(space[0], material);
        if (data != 0) toReturn.setDurability(data);

        return toReturn;
    }

    private static ItemStack addDurability(ItemStack toReturn, String itemName) {
        Matcher m = Durability.matcher(itemName);
        if (!m.find()) return toReturn;
        
        String data = m.group();
        if (data == null || data.isEmpty()) return toReturn;
        data = data.substring(1);
        if (uNumber.isInteger(data)) toReturn.setDurability(Short.valueOf(data));

        return toReturn;
    }

    private static Map<Enchantment, Integer> getEnchantment(String itemName){
        return uEnchantment.decodeEnchantment(itemName);
    }
    
    private static Map<Enchantment, Integer> getEnchant(String original){
        Matcher m = Enchant.matcher(original);
        if (!m.find()) return new HashMap<Enchantment, Integer>();
        String group = m.group().substring(1);
        return getEnchantment(group);
    }
    
    private static ItemStack addEnchantments(ItemStack is, String itemname){
        try{ is.addEnchantments(getEnchant(itemname));
        } catch (Exception ignored) {}
        return is;
    }

    private static ItemStack getFromOddItem(String itemName) {
        return !Odd.isInitialized() ? null : Odd.returnItemStack(itemName.replace(":", ";"));
    }

    private static short getDataFromWord(String name, Material material) {
        return DataValue.get(name, material);
    }

}
