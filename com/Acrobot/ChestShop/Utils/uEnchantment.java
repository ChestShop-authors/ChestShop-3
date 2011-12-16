package com.Acrobot.ChestShop.Utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public class uEnchantment {
    public static String getEnchantment(ItemStack item){
        return encodeEnchantment(item.getEnchantments());
    }
    
    public static String encodeEnchantment(Map<Enchantment, Integer> map){
        int integer = 0;
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()){
            integer = integer * 1000 + (entry.getKey().getId()) * 10 + entry.getValue();
        }
        return (integer != 0 ? Integer.toString(integer, 32) : null);
    }
    
    public static Map<Enchantment, Integer> decodeEnchantment(String base32){
        if (base32 == null) return new HashMap<Enchantment, Integer>();
        Map<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();

        String integer = String.valueOf(Integer.parseInt(base32, 32));

        for (int i = 0; i < (integer.length() / 3); i++){
            String item = integer.substring(i * 3, i * 3 + 3);
            Enchantment ench = Enchantment.getById(Integer.parseInt(item.substring(0, 2)));
            if (ench == null) continue;
            int level = Integer.parseInt(item.substring(2));
            if (ench.getMaxLevel() < level || level < ench.getStartLevel()) continue;
            map.put(ench, level);
        }
        return map;
    }
}
