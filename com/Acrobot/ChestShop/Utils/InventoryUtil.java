package com.Acrobot.ChestShop.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * @author Acrobot
 */
public class InventoryUtil {

    public static int remove(Inventory inv, ItemStack item, int amount, short durability) {
        Material itemMaterial = item.getType();
        int amountLeft = amount;

        for (int slot = 0; slot < inv.getSize(); slot++) {
            if (amountLeft <= 0) {
                return 0;
            }

            ItemStack currentItem = inv.getItem(slot);
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                continue;
            }

            if (currentItem.getType() == itemMaterial && (durability == -1 || currentItem.getDurability() == durability)) {
                int currentAmount = currentItem.getAmount();
                if (amountLeft == currentAmount) {
                    currentItem = null;
                    amountLeft = 0;
                } else if (amountLeft < currentAmount) {
                    currentItem.setAmount(currentAmount - amountLeft);
                    amountLeft = 0;
                } else {
                    currentItem = null;
                    amountLeft -= currentAmount;
                }
                inv.setItem(slot, currentItem);
            }
        }

        return amountLeft;
    }

    public static int add(Inventory inv, ItemStack item, int amount) {
        Material itemMaterial = item.getType();
        int maxStackSize = itemMaterial.getMaxStackSize();

        if (amount <= maxStackSize) {
            item.setAmount(amount);
            inv.addItem(item);
            return 0;
        }

        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < Math.ceil(amount / maxStackSize); i++) {
            if (amount <= maxStackSize) {
                item.setAmount(amount);
                items.add(item);
                return 0;
            } else {
                item.setAmount(maxStackSize);
                items.add(item);
            }
        }
        Object[] iArray = items.toArray();

        amount = 0;
        for (Object o : iArray) {
            ItemStack itemToAdd = (ItemStack) o;
            amount += (!inv.addItem(itemToAdd).isEmpty() ? itemToAdd.getAmount() : 0);
        }

        return amount;
    }

    public static int amount(Inventory inv, ItemStack item, short durability) {
        int amount = 0;
        if(!inv.contains(item.getType())){
            return amount;
        }

        ItemStack[] contents = inv.getContents();
        for (ItemStack i : contents) {
            if (i != null) {
                if (i.getType() == item.getType() && (durability == -1 || i.getDurability() == durability)) {
                    amount += i.getAmount();
                }
            }
        }
        return amount;
    }

    public static int fits(Inventory inv, ItemStack item, int amount, short durability) {
        Material itemMaterial = item.getType();
        int maxStackSize = itemMaterial.getMaxStackSize();

        int amountLeft = amount;

        for (ItemStack currentItem : inv.getContents()) {
            if (amountLeft <= 0) {
                return 0;
            }

            if (currentItem == null || currentItem.getType() == Material.AIR) {
                amountLeft -= maxStackSize;
                continue;
            }

            int currentAmount = currentItem.getAmount();

            if (currentAmount != maxStackSize && currentItem.getType() == itemMaterial && (durability == -1 || currentItem.getDurability() == durability)) {
                amountLeft = ((currentAmount + amountLeft) <= maxStackSize ? 0 : amountLeft - (maxStackSize - currentAmount));
            }
        }

        return amountLeft;
    }
}
