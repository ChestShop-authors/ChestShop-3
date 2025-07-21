package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.Breeze.Utils.InventoryUtil;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Events.Economy.CurrencyFormatEvent;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.ShopInfoEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.google.common.collect.ImmutableMap;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Acrobot
 */
public class ShopInfoListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public static void showShopInfo(ShopInfoEvent event) {
        if (ChestShopSign.isValid(event.getSign())) {
            String nameLine = ChestShopSign.getOwner(event.getSign());
            int amount;
            try {
                amount = ChestShopSign.getQuantity(event.getSign());
            } catch (NumberFormatException notANumber) {
                Messages.INVALID_SHOP_DETECTED.sendWithPrefix(event.getSender());
                return;
            }
            String pricesLine = ChestShopSign.getPrice(event.getSign());

            AccountQueryEvent queryEvent = new AccountQueryEvent(nameLine);
            ChestShop.callEvent(queryEvent);
            if (queryEvent.getAccount() == null) {
                Messages.INVALID_SHOP_DETECTED.sendWithPrefix(event.getSender());
                return;
            }

            String ownerName = queryEvent.getAccount().getName();
            ownerName = ownerName != null ? ownerName : nameLine;

            ItemParseEvent parseEvent = new ItemParseEvent(ChestShopSign.getItem(event.getSign()));
            ItemStack item = ChestShop.callEvent(parseEvent).getItem();
            if (item == null || amount < 1) {
                Messages.INVALID_SHOP_DETECTED.sendWithPrefix(event.getSender());
                return;
            }

            Container shopBlock = uBlock.findConnectedContainer(event.getSign());
            String stock;
            if (shopBlock != null) {
                stock = String.valueOf(InventoryUtil.getAmount(item, shopBlock.getInventory()));
            } else {
                stock = "\u221e"; // Infinity symbol
            }

            Map<String, String> replacementMap = ImmutableMap.of(
                    "item", ItemUtil.getName(item),
                    "stock", stock,
                    "owner", ownerName,
                    "prices", pricesLine,
                    "quantity", String.valueOf(amount)
            );
            if (!Properties.SHOWITEM_MESSAGE
                    || !MaterialUtil.Show.sendMessage(event.getSender(), event.getSender().getName(), Messages.shopinfo, false, new ItemStack[]{item}, replacementMap)) {
                Messages.shopinfo.send(event.getSender(), replacementMap);
            }


            BigDecimal buyPrice = PriceUtil.getExactBuyPrice(pricesLine);
            BigDecimal sellPrice = PriceUtil.getExactSellPrice(pricesLine);

            ChestShop.callEvent(new ItemInfoEvent(event.getSender(), item));

            if (!buyPrice.equals(PriceUtil.NO_PRICE)) {
                CurrencyFormatEvent cfe = ChestShop.callEvent(new CurrencyFormatEvent(buyPrice));
                Messages.shopinfo_buy.send(event.getSender(),
                        "amount", String.valueOf(amount),
                        "price", cfe.getFormattedAmount()
                );
            }
            if (!sellPrice.equals(PriceUtil.NO_PRICE)) {
                CurrencyFormatEvent cfe = ChestShop.callEvent(new CurrencyFormatEvent(sellPrice));
                Messages.shopinfo_sell.send(event.getSender(),
                        "amount", String.valueOf(amount),
                        "price", cfe.getFormattedAmount()
                );
            }
        } else {
            Messages.INVALID_SHOP_DETECTED.sendWithPrefix(event.getSender());
        }
    }
}
