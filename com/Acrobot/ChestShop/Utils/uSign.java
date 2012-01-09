package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Permission;
import com.palmergames.bukkit.towny.Towny;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class uSign {
    private static final Pattern[] patterns = {
            Pattern.compile("^$|^\\w.+$"),
            Pattern.compile("[0-9]+"),
            Pattern.compile(".+"),
            Pattern.compile("[\\w : -]+")
    };

    public static Towny towny; //Moved this here - somehow, java fails at try/catch

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

    public static boolean canAccess(Player p, Sign s) {
        if (p == null) return false;
        if (s == null) return true;

        String line = s.getLine(0);
        return uLongName.stripName(p.getName()).equals(line) || Permission.otherName(p, line);
    }

    public static boolean isValidPreparedSign(String[] lines) {
        boolean toReturn = true;
        for (int i = 0; i < 4 && toReturn; i++) toReturn = patterns[i].matcher(lines[i]).matches();
        return toReturn && lines[2].indexOf(':') == lines[2].lastIndexOf(':');
    }

    public static float buyPrice(String text) {
        return price(text, true);
    }

    public static float sellPrice(String text) {
        return price(text, false);
    }

    private static float price(String text, boolean buy) {
        String toContain = buy ? "b" : "s";
        text = text.replace(" ", "").toLowerCase();

        String[] split = text.split(":");
        int part = (text.contains(toContain) ? (split[0].contains(toContain) ? 0 : 1) : -1);
        if (part == -1 || (part == 1 && split.length != 2)) return -1;

        split[part] = split[part].replace(toContain, "");

        if (uNumber.isFloat(split[part])) {
            Float price = Float.parseFloat(split[part]);
            return (price > 0 ? price : -1);
        } else if (split[part].equals("free")) return 0;
        return -1;
    }

    public static int itemAmount(String text) {
        if (uNumber.isInteger(text)) {
            int amount = Integer.parseInt(text);
            return (amount >= 1 ? amount : 1);
        } else return 1;
    }

    public static String capitalizeFirst(String name) {
        return capitalizeFirst(name, '_');
    }

    public static String capitalizeFirst(String name, char separator) {
        name = name.toLowerCase();
        String[] split = name.split(Character.toString(separator));
        StringBuilder total = new StringBuilder(3);
        for (String s : split) total.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(' ');

        return total.toString().trim();
    }
}
