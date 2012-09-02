package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Utils.uName;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

import static com.Acrobot.ChestShop.Config.Property.ADMIN_SHOP_NAME;

/**
 * @author Acrobot
 */
public class ChestShopSign {
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static final Pattern[] SHOP_SIGN_PATTERN = {
            Pattern.compile("^[\\w ]*$"),
            Pattern.compile("[0-9]+"),
            Pattern.compile("(?i)^[\\dbs(free) :]+$"),
            Pattern.compile("[\\w : -]+")
    };

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Config.getString(ADMIN_SHOP_NAME).replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLine(NAME_LINE));
    }

    public static boolean isValid(Sign sign) {
        return isValid(sign.getLines());
    }

    public static boolean isValid(String[] line) {
        return isValidPreparedSign(line) && (line[2].toUpperCase().contains("B") || line[2].toUpperCase().contains("S")) && !line[0].isEmpty();
    }

    public static boolean isValid(Block sign) {
        return BlockUtil.isSign(sign) && isValid((Sign) sign.getState());
    }

    public static boolean canAccess(Player player, Sign sign) {
        if (player == null) return false;
        if (sign == null) return true;

        return uName.canUseName(player, sign.getLine(0));
    }

    public static boolean isValidPreparedSign(String[] lines) {
        for (int i = 0; i < 4; i++) {
            if (!SHOP_SIGN_PATTERN[i].matcher(lines[i]).matches()) {
                return false;
            }
        }
        return lines[2].indexOf(':') == lines[2].lastIndexOf(':');
    }
}
