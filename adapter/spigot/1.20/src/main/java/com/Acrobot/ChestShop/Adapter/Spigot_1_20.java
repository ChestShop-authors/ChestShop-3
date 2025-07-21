package com.Acrobot.ChestShop.Adapter;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.ImplementationAdapter;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.VersionAdapter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;

import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_armor_trim;

public class Spigot_1_20 implements Listener, VersionAdapter {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();

        if (!BlockUtil.isSign(signBlock)) {
            return;
        }

        if (event.getSide() != Side.FRONT) {
            Sign sign = (Sign) ImplementationAdapter.getState(signBlock, false);
            if (ChestShopSign.isValid(sign)) {
                event.setCancelled(true);
                Messages.CANNOT_CHANGE_SIGN_BACKSIDE.sendWithPrefix(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void addArmorInfo(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof ArmorMeta && ((ArmorMeta) meta).hasTrim()) {
                event.addMessage(iteminfo_armor_trim,
                        "pattern", capitalizeFirstLetter(((ArmorMeta) meta).getTrim().getPattern().getKey().getKey(), '_'),
                        "material", capitalizeFirstLetter(((ArmorMeta) meta).getTrim().getMaterial().getKey().getKey(), '_'));
            }
        }
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("org.bukkit.block.sign.Side");
            Class.forName("org.bukkit.inventory.meta.ArmorMeta");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
