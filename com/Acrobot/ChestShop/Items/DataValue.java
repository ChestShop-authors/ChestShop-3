package com.Acrobot.ChestShop.Items;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

public class DataValue {
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
                if (mat.name().startsWith(type)) {
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