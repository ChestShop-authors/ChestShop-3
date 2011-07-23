package com.Acrobot.ChestShop.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Acrobot
 */
public class uInventory {

    public static int remove(Inventory inv, ItemStack item, int amount, short durability) {
        amount = (amount > 0 ? amount : 1);
        Material itemMaterial = item.getType();

        int first = inv.first(itemMaterial);
        if (first == -1) return amount;

        for (int slot = first; slot < inv.getSize(); slot++) {
            if (amount <= 0) return 0;

            ItemStack currentItem = inv.getItem(slot);
            if (currentItem == null || currentItem.getType() == Material.AIR) continue;

            if (currentItem.getType() == itemMaterial && (durability == -1 || currentItem.getDurability() == durability)) {
                int currentAmount = currentItem.getAmount();
                if (amount == currentAmount) {
                    currentItem = null;
                    amount = 0;
                } else if (amount < currentAmount) {
                    currentItem.setAmount(currentAmount - amount);
                    amount = 0;
                } else {
                    currentItem = null;
                    amount -= currentAmount;
                }
                inv.setItem(slot, currentItem);
            }
        }

        return amount;
    }

    public static int add(Inventory inv, ItemStack item, int amount) {
        amount = (amount > 0 ? amount : 1);

        int maxStackSize = item.getType().getMaxStackSize();

        if (amount <= maxStackSize) {
            item.setAmount(amount);
            HashMap<Integer, ItemStack> left = inv.addItem(item);
            return (left.isEmpty() ? 0 : left.get(0).getAmount());
        }

        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < Math.ceil(amount / maxStackSize); i++) {
            if (amount <= maxStackSize) {
                item.setAmount(amount);
                return 0;
            } else {
                item.setAmount(maxStackSize);
                items.add(item);
            }
        }

        amount = 0;
        for (ItemStack itemToAdd : items) amount += (!inv.addItem(itemToAdd).isEmpty() ? itemToAdd.getAmount() : 0);

        return amount;
    }

    public static int amount(Inventory inv, ItemStack item, short durability) {
        if (!inv.contains(item.getType())) return 0;

        int amount = 0;
        for (ItemStack i : inv.getContents()) {
            if (i != null && i.getType() == item.getType() && (durability == -1 || i.getDurability() == durability)) amount += i.getAmount();
        }
        return amount;
    }

    public static int fits(Inventory inv, ItemStack item, int amount, short durability) {
        Material itemMaterial = item.getType();
        int maxStackSize = itemMaterial.getMaxStackSize();
        int amountLeft = amount;

        for (ItemStack currentItem : inv.getContents()) {
            if (amountLeft <= 0) return 0;

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
