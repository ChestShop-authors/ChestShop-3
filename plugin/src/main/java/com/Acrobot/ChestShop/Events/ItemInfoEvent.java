package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Configuration.Messages;
import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an /iteminfo call
 *
 * @author Acrobot
 */
public class ItemInfoEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSender sender;
    private final ItemStack item;

    private final Map<String, Map.Entry<Messages.Message, String[]>> messages = new LinkedHashMap<>();

    public ItemInfoEvent(CommandSender sender, ItemStack item) {
        this.sender = sender;
        this.item = item;
    }

    /**
     * @return CommandSender who initiated the call
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return Item recognised by /iteminfo
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Specify a message to be sent due to the event
     * @param message Message to be sent
     * @param args Arguments to be replaced in the message
     */
    public void addMessage(Messages.Message message, String... args) {
        messages.put(message.getKey(), new AbstractMap.SimpleEntry<>(message, args));
    }

    public void addMessage(String key, Messages.Message message, String... args) {
        messages.put(key, new AbstractMap.SimpleEntry<>(message, args));
    }

    public void addRawMessage(String key, Component message) {
        messages.put(key, new AbstractMap.SimpleEntry<>(new ComponentMessage(key, message), new String[0]));
    }

    public void addRawMessage(String key, String message) {
        messages.put(key, new AbstractMap.SimpleEntry<>(new StringMessage(key, message), new String[0]));
    }

    /**
     * @return Messages to be sent due to the event
     */
    public Collection<Map.Entry<Messages.Message, String[]>> getMessages() {
        return messages.values();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static class ComponentMessage extends Messages.Message {
        private final Component message;

        public ComponentMessage(String key, Component message) {
            super(key);
            this.message = message;
        }

        @Override
        public Component getComponent(CommandSender sender, boolean prefixSuffix, Map<String, String> replacementMap, String... replacements) {
            if (prefixSuffix) {
                return Messages.prefix.getComponent(sender, false, replacementMap, replacements)
                        .append(message);
            }
            return message;
        }
    }

    public static class StringMessage extends Messages.Message {
        private final String message;

        public StringMessage(String key, String message) {
            super(key);
            this.message = message;
        }

        @Override
        public Component getComponent(CommandSender sender, boolean prefixSuffix, Map<String, String> replacementMap, String... replacements) {
            if (prefixSuffix) {
                return Messages.prefix.getComponent(sender, false, replacementMap, replacements)
                        .append(MineDown.parse(message));
            }
            return MineDown.parse(message);
        }
    }
}
