package com.Acrobot.ChestShop.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Acrobot
 */
public class SignUtil {

    public static boolean isSign(Block block){
        return (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN);
    }
    public static boolean isValid(Sign sign){
        return isValid(sign.getLines());
    }
    public static boolean isValid(String[] lines){
        try{
            String line1 = lines[0];
            String line2 = lines[1];
            String line3 = lines[2];
            String line4 = lines[3];
            return !line1.contains("[") && !line1.contains("]") && !line4.equals("") && Numerical.isInteger(line2) && (line3.contains("B") || line3.contains("S"));
        } catch (Exception e){
            return false;
        }
    }
}
