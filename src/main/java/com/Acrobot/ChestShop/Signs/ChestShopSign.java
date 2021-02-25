package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.Acrobot.Breeze.Utils.ImplementationAdapter.getState;

/**
 * @author Acrobot
 */
public class ChestShopSign {
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static final Pattern[][] SHOP_SIGN_PATTERN = {
            { Pattern.compile("^[1-9][0-9]{0,5}$"), Pattern.compile("^Q [1-9][0-9]{0,4} : C [0-9]{0,5}$") },
            {
                Pattern.compile("(?i)^([BS] *((\\d*([.e]\\d+)?)|free))( *: *([BS] *((\\d*([.e]\\d+)?)|free)))?$"),
                Pattern.compile("(?i)^(((\\d*([.e]\\d+)?)|free) *[BS])( *: *([BS] *((\\d*([.e]\\d+)?)|free)))?$"),
                Pattern.compile("(?i)^(((\\d*([.e]\\d+)?)|free) *[BS]) *: *(((\\d*([.e]\\d+)?)|free) *[BS])$"),
                Pattern.compile("(?i)^([BS] *((\\d*([.e]\\d+)?)|free)) *: *(((\\d*([.e]\\d+)?)|free) *[BS])$"),
            },
            { Pattern.compile("^[\\w? #:\\-]+$") }
    };
    public static final String AUTOFILL_CODE = "?";

    public static boolean isAdminShop(Inventory ownerInventory) {
        return ownerInventory instanceof AdminInventory;
    }

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLine(NAME_LINE));
    }

    public static boolean isValid(Sign sign) {
        return isValid(sign.getLines());
    }

    public static boolean isValid(String[] line) {
        line = StringUtil.stripColourCodes(line);
        return isValidPreparedSign(line) && (line[PRICE_LINE].toUpperCase(Locale.ROOT).contains("B") || line[PRICE_LINE].toUpperCase(Locale.ROOT).contains("S")) && !line[NAME_LINE].isEmpty();
    }

    public static boolean isValid(Block sign) {
        return BlockUtil.isSign(sign) && isValid((Sign) getState(sign, false));
    }

    /**
     * @deprecated Use {@link #isShopBlock(Block}
     */
    @Deprecated
    public static boolean isShopChest(Block chest) {
        if (!BlockUtil.isChest(chest)) {
            return false;
        }

        return uBlock.getConnectedSign(chest) != null;
    }

    public static boolean isShopBlock(Block block) {
        if (!uBlock.couldBeShopContainer(block)) {
            return false;
        }

        return uBlock.getConnectedSign(block) != null;
    }

    /**
     * @deprecated Use {@link #isShopBlock(InventoryHolder}
     */
    @Deprecated
    public static boolean isShopChest(InventoryHolder holder) {
        if (!BlockUtil.isChest(holder)) {
            return false;
        }

        if (holder instanceof DoubleChest) {
            return isShopChest(((DoubleChest) holder).getLocation().getBlock());
        } else if (holder instanceof Chest) {
            return isShopChest(((Chest) holder).getBlock());
        } else {
            return false;
        }
    }

    public static boolean isShopBlock(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return isShopBlock(((DoubleChest) holder).getLeftSide())
                    || isShopBlock(((DoubleChest) holder).getRightSide());
        } else if (holder instanceof BlockState) {
            return isShopBlock(((BlockState) holder).getBlock());
        }
        return false;
    }

    public static boolean canAccess(Player player, Sign sign) {
        return hasPermission(player, Permission.OTHER_NAME_ACCESS, sign);
    }

    public static boolean hasPermission(Player player, Permission base, Sign sign) {
        if (player == null) return false;
        if (sign == null) return true;

        String name = sign.getLine(NAME_LINE);
        if (name == null || name.isEmpty()) return true;

        return NameManager.canUseName(player, base, name);
    }

    public static boolean isOwner(Player player, Sign sign) {
        if (player == null || sign == null) return false;

        String name = sign.getLine(NAME_LINE);
        if (name == null || name.isEmpty()) return false;

        AccountQueryEvent accountQueryEvent = new AccountQueryEvent(name);
        Bukkit.getPluginManager().callEvent(accountQueryEvent);
        Account account = accountQueryEvent.getAccount();
        if (account == null) {
            return player.getName().equalsIgnoreCase(name);
        }
        return account.getUuid().equals(player.getUniqueId());
    }

    public static boolean isValidPreparedSign(String[] lines) {
        // If the shop owner (playername) line (first line) is not blank (auto-filled) or the admin shop string, we need to validate it
        if ((!isAdminShop(lines[0])) && (lines[0].length() > 0)) {
            // The shop owner line can now be a verbatim playername, or (if the name is too long) a shortened name followed by a ':' then a base62 encoded ID

            // Prepare regexp pattern defined in the configuration file
            Pattern playernamePattern = Pattern.compile(Properties.VALID_PLAYERNAME_REGEXP);

            String playername = null;
            int lastColon = lines[0].lastIndexOf(":");

            if (lastColon > -1) {
                // Found a ':' so this is a shortened name. Extract everything before the last ':'.
                playername = lines[0].substring(0, lastColon - 1);
            }
            else {
                // Not found - this is a verbatim playername
                playername = lines[0];
            }

            // If the playername doesn't match, this is not a valid sign, so return
            if (!playernamePattern.matcher(playername).matches()) {
                return false;
            }
        }

        // The first line is valid. Now validate the last 3 lines against the predefined regexp patterns.
        for (int i = 0; i < 3; i++) {
            boolean matches = false;
            for (Pattern pattern : SHOP_SIGN_PATTERN[i]) {
                if (pattern.matcher(lines[i+1]).matches()) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                return false;
            }
        }

        // All lines are looking good. If the price line contains only one ':', then this is a valid prepared sign.
        return lines[PRICE_LINE].indexOf(':') == lines[PRICE_LINE].lastIndexOf(':');
    }
}
