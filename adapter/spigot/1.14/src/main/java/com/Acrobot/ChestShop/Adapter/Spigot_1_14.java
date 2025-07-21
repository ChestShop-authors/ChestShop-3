package com.Acrobot.ChestShop.Adapter;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import com.Acrobot.ChestShop.Utils.VersionAdapter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Map;

import static com.Acrobot.Breeze.Utils.StringUtil.capitalizeFirstLetter;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_crossbow_projectile;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_crossbow_projectiles;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_map_location;
import static com.Acrobot.ChestShop.Configuration.Messages.iteminfo_map_view;

public class Spigot_1_14 implements Listener, VersionAdapter {

    @EventHandler(priority = EventPriority.HIGH)
    public static void addMapInfo(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof MapMeta) {
                if (((MapMeta) meta).getMapView() != null) {
                    MapView mapView = ((MapMeta) meta).getMapView();
                    event.addMessage(iteminfo_map_view,
                            "id", String.valueOf(mapView.getId()),
                            "x", String.valueOf(mapView.getCenterX()),
                            "z", String.valueOf(mapView.getCenterZ()),
                            "world", mapView.getWorld() != null ? mapView.getWorld().getName() : "unknown",
                            "scale", capitalizeFirstLetter(mapView.getScale().name(), '_'),
                            "locked", String.valueOf(mapView.isLocked())
                    );
                }
                if (((MapMeta) meta).hasLocationName()) {
                    event.addMessage(iteminfo_map_location, "location", String.valueOf(((MapMeta) meta).getLocationName()));
                }
            }
        }
    }

    @EventHandler
    public void addCrossBowInfo(ItemInfoEvent event) {
        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta instanceof CrossbowMeta && ((CrossbowMeta) meta).hasChargedProjectiles()) {
                event.addMessage(iteminfo_crossbow_projectiles);
                for (ItemStack chargedProjectile : ((CrossbowMeta) meta).getChargedProjectiles()) {
                    ItemInfo.sendItemName(event.getSender(), chargedProjectile, iteminfo_crossbow_projectile);
                    ItemInfoEvent projectileEvent = ChestShop.callEvent(new ItemInfoEvent(event.getSender(), chargedProjectile));
                    for (Map.Entry<Messages.Message, String[]> entry : projectileEvent.getMessages()) {
                        event.addMessage("crossbow_projectile_" + chargedProjectile.hashCode() + "_" + entry.getKey().getKey(), entry.getKey(), entry.getValue());
                    }
                    event.addRawMessage("crossbow_projectile_" + chargedProjectile.hashCode() + "_divider", ChatColor.GRAY + "---");
                }
            }
        }
    }

    @Override
    public boolean isSupported() {
        try {
            Class.forName("org.bukkit.inventory.meta.CrossbowMeta");
            MapView.class.getMethod("isLocked");
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }
}
