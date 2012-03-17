package com.Acrobot.ChestShop.Items;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

/**
 * @author Acrobot
 */
public class DataValue {
    public static byte get(String type, Material material) {
        if (material == null) return 0;

        type = type.toUpperCase().replace(" ", "_");
        MaterialData materialData = null;

        try {
            switch (material) {
                case SAPLING:
                case LOG:
                    materialData = new Tree(TreeSpecies.valueOf(type));
                    break;
                case STEP:
                case DOUBLE_STEP:
                    materialData = new Step(Items.getMaterial(type));
                    break;
                case WOOL:
                    materialData = new Wool(DyeColor.valueOf(type));
                    break;
                case INK_SACK:
                    byte data = (byte) (15 - DyeColor.valueOf(type).getData());
                    materialData = new Wool(DyeColor.getByData(data));
                    break;
                case COAL:
                    materialData = new Coal(CoalType.valueOf(type));
                    break;
            }
        } catch (Exception e) {
            return 0;
        }

        return (materialData == null ? 0 : materialData.getData());
    }

    public static String getName(ItemStack is) {
        short dur = is.getDurability();
        if (dur == 0) return null;

        Material material = is.getType();

        String name = null;

        try {
            switch (material) {
                case SAPLING:
                case LOG:
                    name = TreeSpecies.getByData((byte) dur).name();
                    break;
                case STEP:
                case DOUBLE_STEP:
                    name = new Step(Material.getMaterial(dur)).getMaterial().name();
                    break;
                case WOOL:
                    name = DyeColor.getByData((byte) dur).name();
                    break;
                case INK_SACK:
                    name = DyeColor.getByData((byte) (15 - dur)).name();
                    break;
                case COAL:
                    name = CoalType.getByData((byte) dur).name();
                    break;
            }
        } catch (Exception e) {
            return null;
        }

        return name;
    }
}
