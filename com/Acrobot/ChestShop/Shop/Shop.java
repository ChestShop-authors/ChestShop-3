package com.Acrobot.ChestShop.Shop;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Chests.ChestObject;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Utils.Config;
import com.Acrobot.ChestShop.Utils.InventoryUtil;
import com.Acrobot.ChestShop.Utils.SignUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Acrobot
 */
public class Shop {
    private ItemStack stock;
    private int stockAmount;
    private ChestObject chest;
    private float buyPrice;
    private float sellPrice;
    private String owner;

    public Shop(ChestObject chest, Sign sign, ItemStack ... itemStacks){
        this.stock = itemStacks[0];
        this.chest = chest;
        this.buyPrice = SignUtil.buyPrice(sign.getLine(2));
        this.sellPrice = SignUtil.sellPrice(sign.getLine(2));
        this.owner = sign.getLine(0);
        this.stockAmount = SignUtil.itemAmount(sign.getLine(1));
    }

    public boolean buy(Player player){
        if(buyPrice == -1){
            player.sendMessage(Config.getLocal("NO_BUYING_HERE"));
            return false;
        }

        if(!fits(stock, player)){
            player.sendMessage(Config.getLocal("NOT_ENOUGH_SPACE_IN_INVENTORY"));
            return false;
        }

        String materialName = stock.getType().name();

        if(!isAdminShop() && !hasEnoughStock()){
            player.sendMessage(Config.getLocal("NOT_ENOUGH_STOCK"));
            sendMessageToOwner(Config.getLocal("NOT_ENOUGH_STOCK_IN_YOUR_SHOP").replace("%material", materialName));
            return false;
        }
        
        if(!getOwner().isEmpty() && Economy.hasAccount(getOwner())){
            Economy.add(getOwner(), buyPrice);
        }
        Economy.substract(player.getName(), buyPrice);

        if(!isAdminShop()){
            chest.removeItem(stock, stock.getDurability(), stockAmount);
        }
        InventoryUtil.add(player.getInventory(), stock, stockAmount);

        player.updateInventory();

        String formatedPrice = Economy.formatBalance(buyPrice);

        player.sendMessage(Config.getLocal("YOU_BOUGHT_FROM_SHOP")
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%owner", owner)
                .replace("%price", formatedPrice));

        sendMessageToOwner(Config.getLocal("SOMEBODY_BOUGHT_FROM_YOUR_SHOP")
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%buyer", player.getName())
                .replace("%price", formatedPrice));
        
        return true;
    }

    public boolean sell(Player player){
        if(sellPrice == -1){
            player.sendMessage(Config.getLocal("NO_SELLING_HERE"));
            return false;
        }

        if(!isAdminShop() && !fits(stock, chest)){
            player.sendMessage(Config.getLocal("NOT_ENOUGH_SPACE_IN_CHEST"));
            return false;
        }

        if(InventoryUtil.amount(player.getInventory(), stock, stock.getDurability()) < stockAmount){
            player.sendMessage(Config.getLocal("NOT_ENOUGH_ITEMS_TO_SELL"));
            return false;
        }

        if(!getOwner().isEmpty() && Economy.hasAccount(getOwner())){
            Economy.substract(getOwner(), sellPrice);
        }

        if(!isAdminShop()){
            chest.addItem(stock, stock.getDurability(), stockAmount);
        }

        InventoryUtil.remove(player.getInventory(), stock, stockAmount, stock.getDurability());

        player.updateInventory();

        Economy.add(player.getName(), sellPrice);

        String materialName = stock.getType().name();
        String formatedBalance = Economy.formatBalance(sellPrice);

        player.sendMessage(Config.getLocal("YOU_SOLD_TO_SHOP")
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%buyer", owner)
                .replace("%price", formatedBalance));

        sendMessageToOwner(Config.getLocal("SOMEBODY_SOLD_TO_YOUR_SHOP")
                .replace("%amount", String.valueOf(stockAmount))
                .replace("%item", materialName)
                .replace("%seller", player.getName())
                .replace("%price", formatedBalance));

        return true;
    }

    private String getOwner(){
        if(SignUtil.isAdminShop(owner)){
            return Config.getString("shopEconomyAccount");
        } else{
            return owner;
        }
    }

    private boolean isAdminShop(){
        return SignUtil.isAdminShop(owner);
    }

    private boolean hasEnoughStock(){
        return chest.hasEnough(stock, stockAmount, stock.getDurability());
    }
    
    private static boolean fits(ItemStack item, Player player){
        return InventoryUtil.fits(player.getInventory(), item, item.getAmount(), item.getDurability()) <= 0;
    }

    private static boolean fits(ItemStack item, ChestObject chest){
        return chest.fits(item, item.getAmount(), item.getDurability());
    }

    private void sendMessageToOwner(String msg){
        if(!isAdminShop()){
            Player player = ChestShop.getBukkitServer().getPlayer(owner);
            if(player != null){
                player.sendMessage(msg);
            }
        }
    }
}
