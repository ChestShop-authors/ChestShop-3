package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

            if (equals(currentItem, item, durability)) {
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
        if (Config.getBoolean(Property.STACK_UNSTACKABLES)) return addAndStackTo64(inv, item, amount);
        /*ItemStack itemstack = new ItemStack(item.getType(), amount, item.getDurability());
        itemstack.addEnchantments(item.getEnchantments());
        HashMap<Integer, ItemStack> items = inv.addItem(itemstack);
        amount = 0;
        for (ItemStack toAdd : items.values()) amount += toAdd.getAmount();

        return amount;*/ //TODO: Fix this in CraftBukkit's code

        return addManually(inv, item, amount);
    }

    private static int addManually(Inventory inv, ItemStack item, int amount) {
        return addManually(inv, item, amount, (item.getType() != Material.POTION ? item.getType().getMaxStackSize() : 1)); //TODO Change it when it's repaired in Bukkit
    }

    public static int addAndStackTo64(Inventory inv, ItemStack item, int amount) {
        return addManually(inv, item, amount, 64);
    }
    
    public static int addManually(Inventory inv, ItemStack item, int amount, int max){
        if (amount <= 0) return 0;

        for (int slot = 0; slot < inv.getSize() && amount > 0; slot++){
            ItemStack curItem = inv.getItem(slot);
            ItemStack dupe = item.clone();

            if (curItem == null || curItem.getType() == Material.AIR) {
                dupe.setAmount((amount > max ? max : amount));
                dupe.addEnchantments(item.getEnchantments());
                amount -= dupe.getAmount();
                inv.setItem(slot, dupe);
            } else if (equals(item, curItem, curItem.getDurability()) && curItem.getAmount() != max) {
                int cA = curItem.getAmount();
                int amountAdded = amount > max - cA ? max - cA : amount;
                dupe.setAmount(cA + amountAdded);
                amount -= amountAdded;
                dupe.addEnchantments(item.getEnchantments());
                inv.setItem(slot, dupe);
            }
        }

        return amount;
    }

    public static int amount(Inventory inv, ItemStack item, short durability) {
        if (!inv.contains(item.getType())) return 0;

        int amount = 0;
        for (ItemStack i : inv.getContents()) {
            if (equals(i, item, durability)) amount += i.getAmount();
        }
        return amount;
    }

    public static int fits(Inventory inv, ItemStack item, int amount, short durability) {
        int maxStackSize = (Config.getBoolean(Property.STACK_UNSTACKABLES) ? 64 : item.getType().getMaxStackSize());
        int amountLeft = amount;

        for (ItemStack currentItem : inv.getContents()) {
            if (amountLeft <= 0) return 0;

            if (currentItem == null || currentItem.getType() == Material.AIR) {
                amountLeft -= maxStackSize;
                continue;
            }

            int currentAmount = currentItem.getAmount();

            if (currentAmount != maxStackSize && equals(currentItem, item, durability)) {
                amountLeft = currentAmount + amountLeft <= maxStackSize ? 0 : amountLeft - (maxStackSize - currentAmount);
            }
        }

        return amountLeft;
    }
    
    private static boolean equals(ItemStack i, ItemStack item, short durability){
        return i != null
                && i.getType() == item.getType()
                && i.getEnchantments().equals(item.getEnchantments())
                && (durability == -1 || i.getDurability() == durability);
    }
}
