package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class uSign {

    //static Pattern firstLine = Pattern.compile("^[A-Za-z0-9].+$");

    static Pattern[] patterns = {
            Pattern.compile("^$|^\\w.+$"),
            Pattern.compile("[0-9]+"),
            Pattern.compile(".+"),
            Pattern.compile("[\\w :]+")
    };
    
    public static boolean isSign(Block block) {
        return block.getState() instanceof Sign;
    }

    public static boolean isAdminShop(String owner) {
        return owner.toLowerCase().replace(" ", "").equals(Config.getString(Property.ADMIN_SHOP_NAME).toLowerCase().replace(" ", ""));
    }

    public static boolean isValid(Sign sign) {
        return isValid(sign.getLines());
    }

    public static boolean isValid(String[] line) {
        try {
            return isValidPreparedSign(line) && (line[2].contains("B") || line[2].contains("S"));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidPreparedSign(String[] lines){
        try{
            boolean toReturn = true;
            for(int i = 0; i < 4; i++){
                toReturn = toReturn && patterns[i].matcher(lines[i]).matches();
            }
            return toReturn;
        } catch (Exception e){
            return false;
        }
    }

    public static float buyPrice(String text) {
        text = text.replace(" ", "").toLowerCase();

        int buyPart = (text.contains("b") ? 0 : -1);
        if (buyPart == -1) {
            return -1;
        }
        text = text.replace("b", "").replace("s", "");
        String[] split = text.split(":");
        if (uNumber.isFloat(split[0])) {
            float buyPrice = Float.parseFloat(split[0]);
            return (buyPrice != 0 ? buyPrice : -1);
        } else if (split[0].equals("free")) {
            return 0;
        }

        return -1;
    }

    public static float sellPrice(String text) {
        text = text.replace(" ", "").toLowerCase();

        int sellPart = (text.contains("b") && text.contains("s") ? 1 : (text.contains("s") ? 0 : -1));
        text = text.replace("b", "").replace("s", "");
        String[] split = text.split(":");

        if (sellPart == -1 || (sellPart == 1 && split.length < 2)) {
            return -1;
        }

        if (uNumber.isFloat(split[sellPart])) {
            Float sellPrice = Float.parseFloat(split[sellPart]);
            return (sellPrice != 0 ? sellPrice : -1);
        } else if (split[sellPart].equals("free")) {
            return 0;
        }
        return -1;
    }

    public static int itemAmount(String text) {
        if (uNumber.isInteger(text)) {
            int amount = Integer.parseInt(text);
            return (amount >= 1 ? amount : 1);
        } else {
            return 1;
        }
    }
}
