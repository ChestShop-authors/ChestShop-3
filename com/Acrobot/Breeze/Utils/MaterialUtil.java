package com.Acrobot.Breeze.Utils;

import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class MaterialUtil {
    public static final Pattern DURABILITY = Pattern.compile(":(\\d)*");
    public static final Pattern ENCHANTMENT = Pattern.compile("-([0-9a-zA-Z])*");

    /**
     * Checks if the itemStack is empty or null
     *
     * @param item Item to check
     * @return Is the itemStack empty?
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Checks if the itemStacks are equal, ignoring their amount
     *
     * @param one first itemStack
     * @param two second itemStack
     * @return Are they equal?
     */
    public static boolean equals(ItemStack one, ItemStack two) {
        if (one.getType() != two.getType()) {
            return false;
        }

        if (one.getDurability() != two.getDurability()) {
            return false;
        }

        return one.getEnchantments().equals(two.getEnchantments());
    }

    /**
     * Gives you a Material from a String (doesn't have to be fully typed in)
     *
     * @param name Name of the material
     * @return Material found
     */
    public static Material getMaterial(String name) {
        Material material = Material.matchMaterial(name);

        if (material != null) {
            return material;
        }

        name = name.replace(" ", "").toUpperCase();

        short length = Short.MAX_VALUE;

        for (Material currentMaterial : Material.values()) {
            String matName = currentMaterial.name();

            if (matName.replace("_", "").startsWith(name) && matName.length() < length) {
                length = (short) matName.length();
                material = currentMaterial;
            }
        }

        return material;
    }

    /**
     * Returns item's name
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack) {
        return getName(itemStack, true);
    }

    /**
     * Returns item's name
     *
     * @param itemStack     ItemStack to name
     * @param showDataValue Should we also show the data value?
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack, boolean showDataValue) {
        String dataName = DataValue.name(itemStack);

        if (dataName != null && showDataValue) {
            return StringUtil.capitalizeFirstLetter(dataName + '_' + itemStack.getType(), '_');
        } else {
            return StringUtil.capitalizeFirstLetter(itemStack.getType().toString(), '_');
        }
    }

    /**
     * Returns item's name, just like on the sign
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getSignName(ItemStack itemStack) {
        StringBuilder name = new StringBuilder(15);

        name.append(itemStack.getType().name());

        if (itemStack.getDurability() != 0) {
            name.append(':').append(itemStack.getDurability());
        }

        if (!itemStack.getEnchantments().isEmpty()) {
            name.append('-').append(MaterialUtil.Enchantment.encodeEnchantment(itemStack));
        }

        return name.toString();
    }

    /**
     * Gives you an ItemStack from a String
     *
     * @param itemName Item name
     * @return ItemStack
     */
    public static ItemStack getItem(String itemName) {
        ItemStack itemStack = Odd.getFromString(itemName);

        if (itemStack != null) {
            return itemStack;
        }

        String[] split = itemName.trim().split(":|-");

        if (split.length == 0) {
            return null;
        }

        Material material = getMaterial(split[0]);

        boolean onlyPartiallyChecked = false;

        if (material == null) {
            int index = split[0].indexOf(' ');
            if (index == -1) {
                return null;
            }

            material = getMaterial(split[0].substring(index + 1));

            if (material == null) {
                return null;
            }

            onlyPartiallyChecked = true;
        }

        itemStack = new ItemStack(material, 1);

        short durability = getDurability(itemName);

        if (durability == 0 && onlyPartiallyChecked) {
            String[] spaces = itemName.split(" ");
            if (spaces.length != 0) {
                durability = DataValue.get(spaces[0], material);
            }
        }

        itemStack.setDurability(durability);

        Map<org.bukkit.enchantments.Enchantment, Integer> enchantments = getEnchantments(itemName);

        if (!enchantments.isEmpty()) {
            try {
                itemStack.addEnchantments(enchantments);
            } catch (IllegalArgumentException exception) {
                //Do nothing, because the enchantment can't be applied
            }
        }

        return itemStack;
    }

    /**
     * Returns the durability from a string
     *
     * @param itemName Item name
     * @return Durability found
     */
    public static short getDurability(String itemName) {
        Matcher m = DURABILITY.matcher(itemName);

        if (!m.find()) {
            return 0;
        }

        String data = m.group();

        if (data == null || data.isEmpty()) {
            return 0;
        }

        data = data.substring(1);

        return NumberUtil.isShort(data) ? Short.valueOf(data) : 0;
    }

    /**
     * Returns enchantments from a string
     *
     * @param itemName Item name
     * @return Enchantments found
     */
    public static Map<org.bukkit.enchantments.Enchantment, Integer> getEnchantments(String itemName) {
        Matcher m = ENCHANTMENT.matcher(itemName);

        if (!m.find()) {
            return new HashMap<org.bukkit.enchantments.Enchantment, Integer>();
        }

        String group = m.group().substring(1);
        return Enchantment.getEnchantments(group);
    }

    public static class Enchantment {
        /**
         * Returns enchantments this itemName contains
         *
         * @param base32 The encoded enchantment
         * @return Enchantments found
         */
        public static Map<org.bukkit.enchantments.Enchantment, Integer> getEnchantments(String base32) {
            if (base32 == null || base32.isEmpty() || NumberUtil.isLong(base32)) {
                return new HashMap<org.bukkit.enchantments.Enchantment, Integer>();
            }

            Map<org.bukkit.enchantments.Enchantment, Integer> map = new HashMap<org.bukkit.enchantments.Enchantment, Integer>();

            StringBuilder number = new StringBuilder(Long.toString(Long.parseLong(base32, 32)));

            while (number.length() % 3 != 0) {
                number.insert(0, '0');
            }

            for (int i = 0; i < number.length() / 3; i++) {
                String item = number.substring(i * 3, i * 3 + 3);

                org.bukkit.enchantments.Enchantment enchantment = org.bukkit.enchantments.Enchantment.getById(Integer.parseInt(item.substring(0, 2)));

                if (enchantment == null) {
                    continue;
                }

                int level = Integer.parseInt(item.substring(2));

                if (level > enchantment.getMaxLevel() || level < enchantment.getStartLevel()) {
                    continue;
                }

                map.put(enchantment, level);
            }

            return map;
        }

        /**
         * Encodes enchantments
         * They are being encoded in a string like XXL (XXLXXL), where L is the enchantment level and XX is the ID
         * Then the string is being encoded in base-32 string
         *
         * @param enchantments Enchantments to encode
         * @return Encoded enchantments
         */
        public static String encodeEnchantment(Map<org.bukkit.enchantments.Enchantment, Integer> enchantments) {
            long number = 0;

            for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : enchantments.entrySet()) {
                number = number * 1000 + (entry.getKey().getId()) * 10 + entry.getValue();
            }

            return number != 0 ? Long.toString(number, 32) : null;
        }

        /**
         * Encodes enchantments
         * They are being encoded in a string like XXL (XXLXXL), where L is the enchantment level and XX is the ID
         * Then the string is being encoded in base-32 string
         *
         * @param item Item to encode
         * @return Encoded enchantments
         */
        public static String encodeEnchantment(ItemStack item) {
            return encodeEnchantment(item.getEnchantments());
        }
    }

    public static class DataValue {
        /**
         * Gets the data value from a string
         *
         * @param type     Data Value string
         * @param material Material
         * @return data value
         */
        public static byte get(String type, Material material) {
            if (material == null || material.getData() == null) {
                return 0;
            }

            type = type.toUpperCase().replace(" ", "_");

            MaterialData materialData = material.getNewData((byte) 0);

            if (materialData instanceof TexturedMaterial) {
                TexturedMaterial texturedMaterial = (TexturedMaterial) materialData;

                for (Material mat : texturedMaterial.getTextures()) {
                    if (mat.name().startsWith(type) && !mat.equals(material)) {
                        return (byte) texturedMaterial.getTextures().indexOf(mat);
                    }
                }
            } else if (materialData instanceof Colorable) {
                DyeColor color;

                try {
                    color = DyeColor.valueOf(type);
                } catch (IllegalArgumentException exception) {
                    return 0;
                }

                if (material == Material.INK_SACK) {
                    color = DyeColor.getByData((byte) (15 - color.getData()));
                }

                return color.getData();
            } else if (materialData instanceof Tree) {
                try {
                    return TreeSpecies.valueOf(type).getData();
                } catch (IllegalArgumentException ex) {
                    return 0;
                }
            } else if (materialData instanceof SpawnEgg) {
                try {
                    EntityType entityType = EntityType.valueOf(type);

                    return (byte) entityType.getTypeId();
                } catch (IllegalArgumentException ex) {
                    return 0;
                }
            } else if (materialData instanceof Coal) {
                try {
                    return CoalType.valueOf(type).getData();
                } catch (IllegalArgumentException ex) {
                    return 0;
                }
            }

            return 0;
        }

        /**
         * Returns a string with the DataValue
         *
         * @param itemStack ItemStack to describe
         * @return Data value string
         */
        public static String name(ItemStack itemStack) {
            MaterialData data = itemStack.getData();

            if (data == null) {
                return null;
            }

            if (data instanceof TexturedMaterial) {
                return ((TexturedMaterial) data).getMaterial().name();
            } else if (data instanceof Colorable) {
                return ((Colorable) data).getColor().name();
            } else if (data instanceof Tree) {
                //TreeSpecies specie = TreeSpecies.getByData((byte) (data.getData() & 3)); //This works, but not as intended
                TreeSpecies specie = ((Tree) data).getSpecies();
                return (specie != null && specie != TreeSpecies.GENERIC ? specie.name() : null);
            } else if (data instanceof SpawnEgg) {
                EntityType type = ((SpawnEgg) data).getSpawnedType();
                return (type != null ? type.name() : null);
            } else if (data instanceof Coal) {
                CoalType coal = ((Coal) data).getType();
                return (coal != null && coal != CoalType.COAL ? coal.name() : null);
            } else {
                return null;
            }
        }
    }

    public static class Odd {
        private static boolean isInitialized = false;

        /**
         * Returns the item stack from OddItem plugin
         *
         * @param itemName Item name to parse
         * @return itemStack that was parsed
         */
        public static ItemStack getFromString(String itemName) {
            if (!isInitialized) {
                return null;
            }

            String name = itemName.replace(':', ';');

            try {
                return OddItem.getItemStack(name);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * Lets the class know that it's safe to use the OddItem methods now
         */
        public static void initialize() {
            isInitialized = true;
        }
    }
}
