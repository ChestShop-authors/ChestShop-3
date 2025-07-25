package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.ImplementationAdapter;
import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Events.SignValidationEvent;
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
import java.util.Optional;
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
            { Pattern.compile("^[1-9][0-9]{0,5}$"), QuantityUtil.QUANTITY_LINE_WITH_COUNTER_PATTERN },
            {
                Pattern.compile("(?i)^((\\d*([.e]\\d+)?)([KM])?|free)$"),
                Pattern.compile("(?i)^([BS] *((\\d*([.e]\\d+)?([KM])?)|free))( *: *([BS] *((\\d*([.e]\\d+)?([KM])?)|free)))?$"),
                Pattern.compile("(?i)^(((\\d*([.e]\\d+)?([KM] )?)|free) *[BS])( *: *([BS] *((\\d*([.e]\\d+)?([KM])?)|free)))?$"),
                Pattern.compile("(?i)^(((\\d*([.e]\\d+)?([KM] )?)|free) *[BS]) *: *(((\\d*([.e]\\d+)?([KM] )?)|free) *[BS])$"),
                Pattern.compile("(?i)^([BS] *((\\d*([.e]\\d+)?([KM])?)|free)) *: *(((\\d*([.e]\\d+)?([KM] )?)|free) *[BS])$"),
            },
            { Pattern.compile("^[\\p{L}\\d_? #:\\-]+$") }
    };
    public static final String AUTOFILL_CODE = "?";

    public static boolean isAdminShop(Inventory ownerInventory) {
        return ownerInventory instanceof AdminInventory;
    }

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLines());
    }

    public static boolean isAdminShop(String[] lines) {
        return isAdminShop(getOwner(lines));
    }

    public static boolean isValid(Sign sign) {
        return isValid(sign.getLines());
    }

    public static boolean isValid(String[] lines) {
        lines = StringUtil.stripColourCodes(lines);
        return ChestShop.callEvent(new SignValidationEvent(lines)).isValid()
                && (getPrice(lines).toUpperCase(Locale.ROOT).contains("B")
                        || getPrice(lines).toUpperCase(Locale.ROOT).contains("S"))
                && !getOwner(lines).isEmpty();
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
            return isShopBlock(ImplementationAdapter.getLeftSide((DoubleChest) holder, false))
                    || isShopBlock(ImplementationAdapter.getRightSide((DoubleChest) holder, false));
        } else if (holder instanceof BlockState) {
            return isShopBlock(((BlockState) holder).getBlock());
        }
        return false;
    }

    public static Block getShopBlock(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return Optional.ofNullable(getShopBlock(ImplementationAdapter.getLeftSide((DoubleChest) holder, false)))
                    .orElse(getShopBlock(ImplementationAdapter.getRightSide((DoubleChest) holder, false)));
        } else if (holder instanceof BlockState) {
            return ((BlockState) holder).getBlock();
        }
        return null;
    }

    public static boolean canAccess(Player player, Sign sign) {
        return hasPermission(player, Permission.OTHER_NAME_ACCESS, sign);
    }

    public static boolean hasPermission(Player player, Permission base, Sign sign) {
        if (player == null) return false;
        if (sign == null) return true;

        String name = getOwner(sign);
        if (name == null || name.isEmpty()) return true;

        return NameManager.canUseName(player, base, name);
    }

    public static boolean isOwner(Player player, Sign sign) {
        if (player == null || sign == null) return false;

        String name = getOwner(sign);
        if (name == null || name.isEmpty()) return false;

        AccountQueryEvent accountQueryEvent = new AccountQueryEvent(name);
        Bukkit.getPluginManager().callEvent(accountQueryEvent);
        Account account = accountQueryEvent.getAccount();
        if (account == null) {
            return player.getName().equalsIgnoreCase(name);
        }
        return account.getUuid().equals(player.getUniqueId());
    }

    /**
     * @deprecated Use the {@link SignValidationEvent} instead!
     */
    @Deprecated
    public static boolean isValidPreparedSign(String[] lines) {
        return ChestShop.callEvent(new SignValidationEvent(lines)).isValid();
    }

    /**
     * Get the owner string of a shop sign
     * @param sign The sign
     * @return The owner string
     */
    public static String getOwner(Sign sign) {
        return getOwner(sign.getLines());
    }

    /**
     * Get the owner string of a shop sign
     * @param lines The sign lines
     * @return The owner string
     */
    public static String getOwner(String[] lines) {
        return StringUtil.stripColourCodes(StringUtil.strip(StringUtil.stripColourCodes(lines[NAME_LINE])));
    }

    /**
     * Get the quantity and count line of the shop sign
     * @param sign The sign
     * @return The quantity line
     * @throws IllegalArgumentException Thrown when an invalid quantity is present
     */
    public static String getQuantityLine(Sign sign) throws IllegalArgumentException {
        return getQuantityLine(sign.getLines());
    }

    /**
     * Get the quantity and count line of sign lines
     * @param lines The sign lines
     * @return The quantity line
     * @throws IllegalArgumentException Thrown when an invalid quantity is present
     */
    public static String getQuantityLine(String[] lines) throws IllegalArgumentException {
        return lines.length > QUANTITY_LINE ? StringUtil.strip(StringUtil.stripColourCodes(lines[QUANTITY_LINE])) : "";
    }

    /**
     * Get the quantity of the shop sign
     * @param sign The sign
     * @return The quantity line
     * @throws IllegalArgumentException Thrown when an invalid quantity is present
     */
    public static int getQuantity(Sign sign) throws IllegalArgumentException {
        return getQuantity(sign.getLines());
    }

    /**
     * Get the quantity of sign lines
     * @param lines The sign lines
     * @return The quantity
     * @throws IllegalArgumentException Thrown when an invalid quantity is present
     */
    public static int getQuantity(String[] lines) throws IllegalArgumentException {
        return QuantityUtil.parseQuantity(getQuantityLine(lines));
    }

    /**
     * Get the price line of the shop sign
     * @param sign The sign
     * @return The price line
     */
    public static String getPrice(Sign sign) {
        return StringUtil.strip(StringUtil.stripColourCodes(sign.getLine(PRICE_LINE)));
    }

    /**
     * Get the price line of sign lines
     * @param lines The sign lines
     * @return The price line
     */
    public static String getPrice(String[] lines) {
        return lines.length > PRICE_LINE ? StringUtil.strip(StringUtil.stripColourCodes(lines[PRICE_LINE])) : "";
    }

    /**
     * Get the item line of the shop sign
     * @param sign The sign
     * @return The item line
     */
    public static String getItem(Sign sign) {
        return getItem(sign.getLines());
    }

    /**
     * Get the item line of sign lines
     * @param lines The sign lines
     * @return The item line
     */
    public static String getItem(String[] lines) {
        return lines.length > ITEM_LINE ? StringUtil.strip(StringUtil.stripColourCodes(lines[ITEM_LINE])) : "";
    }
}
