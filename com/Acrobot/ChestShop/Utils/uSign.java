package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class uSign {
    private static final Pattern[] signPattern = {
            Pattern.compile("^$|^\\w.+$"),
            Pattern.compile("[0-9]+"),
            Pattern.compile(".+"),
            Pattern.compile("[\\w : -]+")
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
        return isValidPreparedSign(line) && (line[2].contains("B") || line[2].contains("S")) && !line[0].isEmpty();
    }

    public static boolean isValid(Block sign) {
        return isSign(sign) && isValid((Sign) sign.getState());
    }

    public static boolean canAccess(Player player, Sign sign) {
        if (player == null) return false;
        if (sign == null) return true;

        return uName.canUseName(player, sign.getLine(0));
    }

    public static boolean isValidPreparedSign(String[] lines) {
        for (int i = 0; i < 4; i++) {
            if (!signPattern[i].matcher(lines[i]).matches()) {
                return false;
            }
        }
        return lines[2].indexOf(':') == lines[2].lastIndexOf(':');
    }

    public static double buyPrice(String text) {
        return getPrice(text, 'b');
    }

    public static double sellPrice(String text) {
        return getPrice(text, 's');
    }

    private static double getPrice(String text, char indicator) {
        String sign = String.valueOf(indicator);

        text = text.replace(" ", "").toLowerCase();

        String[] split = text.split(":");
        int part = (text.contains(sign) ? (split[0].contains(sign) ? 0 : 1) : -1);
        if (part == -1 || (part == 1 && split.length != 2)) return -1;

        split[part] = split[part].replace(sign, "");

        if (uNumber.isDouble(split[part])) {
            double price = Double.parseDouble(split[part]);
            return (price > 0 ? price : -1);
        } else if (split[part].equals("free")) {
            return 0;
        }

        return -1;
    }

    public static int itemAmount(String text) {
        if (uNumber.isInteger(text)) {
            int amount = Integer.parseInt(text);
            return (amount >= 1 ? amount : 1);
        } else return 1;
    }

    public static String capitalizeFirstLetter(String name) {
        return capitalizeFirstLetter(name, '_');
    }

    public static String capitalizeFirstLetter(String name, char separator) {
        name = name.toLowerCase();
        String[] split = name.split(Character.toString(separator));
        StringBuilder total = new StringBuilder(3);

        for (String s : split) {
            total.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(' ');
        }

        return total.toString().trim();
    }
}
