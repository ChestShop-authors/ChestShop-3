package com.Acrobot.ChestShop.Listeners.Modules;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.ChestShopReloadEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType.BUY;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;

/**
 * @author Acrobot
 */
public class DiscountModule implements Listener {
    private YamlConfiguration config;
    private Set<String> groupList = new HashSet<String>();

    public DiscountModule() {
        load();
    }

    private void load() {
        config = YamlConfiguration.loadConfiguration(ChestShop.loadFile("discounts.yml"));

        config.options().header("This file is for discount management. You are able to do that:\n" +
                "group1: 75\n" +
                "That means that the person with ChestShop.discount.group1 permission will pay only 75% of the price. \n" +
                "For example, if the price is 100 dollars, the player pays only 75 dollars.\n" +
                "(Only works in buy-only Admin Shops!)");

        try {
            config.save(ChestShop.loadFile("discounts.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        groupList = config.getKeys(false);
    }

    @EventHandler
    public void onReload(ChestShopReloadEvent event) {
        load();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreTransaction(PreTransactionEvent event) {
        if (event.getTransactionType() != BUY || !NameManager.isAdminShop(event.getOwnerAccount().getUuid())) {
            return;
        }

        Player client = event.getClient();

        if (!PriceUtil.hasBuyPrice(event.getSign().getLine(PRICE_LINE))) {
            return;
        }

        for (String group : groupList) {
            if (Permission.has(client, Permission.DISCOUNT + group)) {
                event.setExactPrice(event.getExactPrice().multiply(BigDecimal.valueOf(config.getDouble(group) / 100)));
                return;
            }
        }
    }
}
