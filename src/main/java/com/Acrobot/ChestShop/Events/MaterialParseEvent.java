package com.Acrobot.ChestShop.Events;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MaterialParseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final String materialString;
    private final short data;
    private Material material = null;

    public MaterialParseEvent(String materialString, short data) {
        this.materialString = materialString;
        this.data = data;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the material string that should be parsed
     * @return The material string to parse
     */
    public String getMaterialString() {
        return materialString;
    }

    /**
     * Get the data of legacy materials that might result in different flattening materials
     * @return The data
     */
    public short getData() {
        return data;
    }

    /**
     * Set the material that the string represents
     * @param material The material for the string
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * The material that was parsed
     * @return The parsed material or null if none was found
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Whether or not the material string of this event has a parsed material
     * @return True if an material was successfully parsed; false if not
     */
    public boolean hasMaterial() {
        return material != null;
    }

}
