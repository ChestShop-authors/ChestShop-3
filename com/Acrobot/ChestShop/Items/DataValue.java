package com.Acrobot.ChestShop.Items;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.material.*;

/**
 * @author Acrobot
 */
public class DataValue {
    public static byte get(String arg, Material material){
        if(material == null){
            return 0;
        }
        
        arg = arg.toUpperCase().replace(" ", "_");


        MaterialData materialData = null;

        switch (material){
            case SAPLING:
            case LOG:
                materialData = new Tree(TreeSpecies.valueOf(arg));
                break;
            case STEP:
            case DOUBLE_STEP:
                materialData = new Step(Items.getMat(arg));
                break;
            case WOOL:
            case INK_SACK:
                materialData = new Wool(DyeColor.valueOf(arg));
                break;
            case COAL:
                materialData = new Coal(CoalType.valueOf(arg));
                break;
        }

        return (materialData == null ? 0 : materialData.getData());
    }
}
