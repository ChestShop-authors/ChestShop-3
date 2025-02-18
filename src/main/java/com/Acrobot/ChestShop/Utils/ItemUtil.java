package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ItemStringQueryEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.Acrobot.Breeze.Utils.MaterialUtil.MAXIMUM_SIGN_WIDTH;
import static com.Acrobot.Breeze.Utils.StringUtil.getMinecraftStringWidth;

public class ItemUtil {

    /**
     * Get a list with item information
     *
     * @param items The items to get the information from
     * @return The list, including the amount and names of the items
     */
    public static String getItemList(ItemStack[] items) {
        ItemStack[] mergedItems = InventoryUtil.mergeSimilarStacks(items);

        List<String> itemText = new ArrayList<>();

        for (ItemStack item : mergedItems) {
            itemText.add(item.getAmount() + " " + getName(item));
        }

        return String.join(", ", itemText);
    }

    /**
     * Returns item's name
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack) {
        return getName(itemStack, 0);
    }


    /**
     * Returns item's name, with a maximum width
     *
     * @param itemStack ItemStack to name
     * @param maxWidth The max width that the name should have; 0 or below if it should be unlimited
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack, int maxWidth) {
        String code = ChestShop.callEvent(new ItemStringQueryEvent(itemStack, maxWidth)).getItemString();
        if (code != null) {
            if (maxWidth > 0) {
                int codeWidth = getMinecraftStringWidth(code);
                if (codeWidth > maxWidth) {
                    int exceeding = codeWidth - maxWidth;

                    int poundIndex = code.indexOf('#');
                    int colonIndex = code.indexOf(':');
                    String material = code;
                    String rest = "";
                    if (poundIndex > 0 && (colonIndex < 0 || poundIndex < colonIndex)) {
                        material = code.substring(0, poundIndex);
                        rest = code.substring(poundIndex);
                    } else if (colonIndex > 0 && (poundIndex < 0 || colonIndex < poundIndex)) {
                        material = code.substring(0, colonIndex);
                        rest = code.substring(colonIndex);
                    }
                    code = MaterialUtil.getShortenedName(material, getMinecraftStringWidth(material) - exceeding) + rest;
                }
            }

            ItemParseEvent parseEvent = new ItemParseEvent(code);
            Bukkit.getPluginManager().callEvent(parseEvent);
            ItemStack codeItem = parseEvent.getItem();
            if (!MaterialUtil.equals(itemStack, codeItem)) {
                throw new IllegalArgumentException("Cannot generate code for item " + itemStack
                        + " with maximum length of " + maxWidth
                        + " (code " + code + " results in item " + codeItem + ")");
            }
        }
        return code;
    }

    /**
     * Returns item's name, just like on the sign
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getSignName(ItemStack itemStack) {
        return getName(itemStack, MAXIMUM_SIGN_WIDTH);
    }
}
