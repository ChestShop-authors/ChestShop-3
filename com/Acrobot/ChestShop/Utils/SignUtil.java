package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Acrobot
 */
public class SignUtil {

    public static boolean isSign(Block block) {
        return (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN);
    }

    public static boolean isAdminShop(String owner) {
        return owner.toLowerCase().replace(" ", "").equals(Config.getString("adminShopName").toLowerCase().replace(" ", ""));
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

    public static boolean isValidPreparedSign(String[] line) {
        try {
            return !line[0].contains("[") && !line[0].contains("]") && !line[3].isEmpty() && !line[3].split(":")[0].isEmpty() && Numerical.isInteger(line[1]) && line[2].split(":").length <= 2;
        } catch (Exception e) {
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
        if (Numerical.isFloat(split[0])) {
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

        if (Numerical.isFloat(split[sellPart])) {
            Float sellPrice = Float.parseFloat(split[sellPart]);
            return (sellPrice != 0 ? sellPrice : -1);
        } else if (split[sellPart].equals("free")) {
            return 0;
        }
        return -1;
    }

    public static int itemAmount(String text) {
        if (Numerical.isInteger(text)) {
            return Integer.parseInt(text);
        } else {
            return 0;
        }
    }
}
