package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Database.Account;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * Represents the state after the modification of a shop sign. Called before the {@link ShopCreatedEvent} is called for
 * the new shop sign but modifications of the state are no longer possible at this point!
 *
 * @author Acrobot
 * @author Phoenix616
 */
public class ShopEditedEvent extends Event  {
    private static final HandlerList handlers = new HandlerList();

    private final Player modifier;
    private final Sign sign;
    private final String[] oldLines;
    private final String[] newLines;

    @Nullable private final Account ownerAccount;
    @Nullable private final Container container;

    public ShopEditedEvent(Player modifier, Sign sign, @Nullable Container container, String[] oldLines, String[] newLines, @Nullable Account ownerAccount) {
        this.modifier = modifier;
        this.sign = sign;
        this.container = container;
        this.oldLines = oldLines.clone();
        this.newLines = newLines.clone();
        this.ownerAccount = ownerAccount;
    }

    /**
     * @return The person who initiated the event
     */
    public Player getModifier() {
        return modifier;
    }

    /**
     * @return The new shop sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * @return The old shop sign text
     */
    public String[] getOldLines() {
        return oldLines;
    }

    /**
     * @return The new shop sign text
     */
    public String[] getNewLines() {
        return newLines;
    }

    /**
     * Returns the shop's container (if applicable)
     *
     * @return Shop's container
     */
    @Nullable
    public Container getContainer() {
        return container;
    }

    /**
     * Get the account of the shop's owner
     *
     * @return The account of the shop's owner; null if no Account could be found
     */
    @Nullable
    public Account getOwnerAccount() {
        return ownerAccount;
    }

    /**
     * Check whether the modified shop is owned by the player modifying it
     *
     * @return <tt>true</tt> if the owner account is the modifier's one (or null); <tt>false</tt> if it's not
     */
    public boolean modifiedByOwner() {
        return ownerAccount == null || ownerAccount.getUuid().equals(modifier.getUniqueId());
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
