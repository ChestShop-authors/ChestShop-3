package com.Acrobot.Breeze.Utils;

import com.Acrobot.Breeze.Collection.SimpleCache;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ItemParseEvent;
import com.Acrobot.ChestShop.Events.MaterialParseEvent;
import com.google.common.collect.ImmutableMap;
import de.themoep.ShowItem.api.ShowItem;
import info.somethingodd.OddItem.OddItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.Acrobot.Breeze.Utils.StringUtil.getMinecraftCharWidth;
import static com.Acrobot.Breeze.Utils.StringUtil.getMinecraftStringWidth;

/**
 * @author Acrobot
 */
public class MaterialUtil {
    public static final Pattern DURABILITY = Pattern.compile(":(\\d)*");
    public static final Pattern METADATA = Pattern.compile("#([0-9a-zA-Z])*");

    public static final boolean LONG_NAME = true;
    public static final boolean SHORT_NAME = false;
    /**
     * @deprecated Use {@link MaterialUtil#MAXIMUM_SIGN_WIDTH}
     */
    @Deprecated
    public static final short MAXIMUM_SIGN_LETTERS = 15;
    // 15 dashes fit on one sign line with the default resource pack:
    public static final int MAXIMUM_SIGN_WIDTH = (short) getMinecraftStringWidth("---------------");

    private static final SimpleCache<String, Material> MATERIAL_CACHE = new SimpleCache<>(Properties.CACHE_SIZE);

    private static final Yaml YAML = new Yaml(new YamlBukkitConstructor(), new YamlRepresenter(), new DumperOptions());

    private static class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }
    }

    /**
     * Checks if the itemStack is empty or null
     *
     * @param item Item to check
     * @return Is the itemStack empty?
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Checks if the itemStacks are equal, ignoring their amount
     *
     * @param one first itemStack
     * @param two second itemStack
     * @return Are they equal?
     */
    public static boolean equals(ItemStack one, ItemStack two) {
        if (one == null || two == null) {
            return one == two;
        }
        if (one.isSimilar(two)) {
            return true;
        }

        // Additional checks as serialisation and de-serialisation might lead to different item meta
        // This would only be done if the items share the same item meta type so it shouldn't be too inefficient
        // Special check for books as their pages might change when serialising (See SPIGOT-3206 and ChestShop#250)
        // Special check for explorer maps/every item with a localised name (See SPIGOT-4672)
        // Special check for legacy spawn eggs (See ChestShop#264)
        if (one.getType() != two.getType()
                || one.getDurability() != two.getDurability()
                || (one.hasItemMeta() && two.hasItemMeta() && one.getItemMeta().getClass() != two.getItemMeta().getClass())) {
            return false;
        }
        if (!one.hasItemMeta() && !two.hasItemMeta()) {
            return true;
        }
        Map<String, Object> oneSerMeta = one.getItemMeta().serialize();
        Map<String, Object> twoSerMeta = two.getItemMeta().serialize();
        if (oneSerMeta.equals(twoSerMeta)) {
            return true;
        }

        // Try to use same parsing as the YAML dumper in the ItemDatabase when generating the code as the last resort
        ItemStack oneDumped = YAML.loadAs(YAML.dump(one), ItemStack.class);
        if (oneDumped.isSimilar(two) || oneDumped.getItemMeta().serialize().equals(twoSerMeta)) {
            return true;
        }

        ItemStack twoDumped = YAML.loadAs(YAML.dump(two), ItemStack.class);
        if (oneDumped.isSimilar(twoDumped) || oneDumped.getItemMeta().serialize().equals(twoDumped.getItemMeta().serialize())) {
            return true;
        }

        return false;
    }

    /**
     * Gives you a Material from a String (doesn't have to be fully typed in)
     *
     * @param name Name of the material
     * @return Material found
     */
    public static Material getMaterial(String name) {
        String formatted = name.replaceAll("([a-z])([A-Z1-9])", "$1_$2").replace(' ', '_').toUpperCase(Locale.ROOT);

        Material material = MATERIAL_CACHE.get(formatted);
        if (material != null) {
            return material;
        }

        material = Material.matchMaterial(name);

        if (material != null) {
            MATERIAL_CACHE.put(formatted, material);
            return material;
        }

        material = new EnumParser<Material>().parse(name, Material.values());
        if (material != null) {
            MATERIAL_CACHE.put(formatted, material);
        }

        return material;
    }

    /**
     * Get a list with item information
     *
     * @param items The items to get the information from
     * @return The list, including the amount and names of the items
     */
    public static String getItemList(ItemStack[] items) {
        ItemStack[] mergedItems = InventoryUtil.mergeSimilarStacks(items);

        List<String> itemText = new ArrayList<>();

        for (ItemStack item : mergedItems) {
            itemText.add(item.getAmount() + " " + getName(item));
        }

        return String.join(", ", itemText);
    }

    /**
     * Returns item's name
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack) {
        return getName(itemStack, 0);
    }

    /**
     * Returns item's name
     *
     * @param itemStack     ItemStack to name
     * @param showDataValue Should we also show the data value?
     * @return ItemStack's name
     * @deprecated Use {@link #getName(ItemStack, int)}
     */
    @Deprecated
    public static String getName(ItemStack itemStack, boolean showDataValue) {
        return getName(itemStack, 0);
    }

    /**
     * Returns item's name, just like on the sign
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getSignName(ItemStack itemStack) {
        return getName(itemStack, MAXIMUM_SIGN_WIDTH);
    }

    /**
     * Returns item's name, with a maximum width
     *
     * @param itemStack ItemStack to name
     * @param maxWidth The max width that the name should have; 0 or below if it should be unlimited
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack, int maxWidth) {
        String alias = Odd.getAlias(itemStack);
        String itemName = alias != null ? alias : itemStack.getType().toString();

        String durability = "";
        if (itemStack.getDurability() != 0) {
            durability = ":" + itemStack.getDurability();
        }

        String metaData = "";
        if (itemStack.hasItemMeta()) {
            metaData = "#" + Metadata.getItemCode(itemStack);
        }

        String code = StringUtil.capitalizeFirstLetter(itemName, '_');
        int codeWidth = getMinecraftStringWidth(code + durability + metaData);
        if (maxWidth > 0 && codeWidth > maxWidth) {
            int exceeding = codeWidth - maxWidth;
            code = getShortenedName(code, getMinecraftStringWidth(code) - exceeding);
        }

        code += durability + metaData;

        ItemParseEvent parseEvent = new ItemParseEvent(code);
        Bukkit.getPluginManager().callEvent(parseEvent);
        ItemStack codeItem = parseEvent.getItem();
        if (!equals(itemStack, codeItem)) {
            throw new IllegalArgumentException("Cannot generate code for item " + itemStack + " with maximum length of " + maxWidth
                    + " (code " + code + " results in item " + codeItem + ")");
        }

        return code;
    }

    /**
     * Get an item name shortened to a max length that is still reversable by {@link #getMaterial(String)}
     *
     * @param itemName  The name of the item
     * @param maxWidth  The max width
     * @return The name shortened to the max length
     */
    public static String getShortenedName(String itemName, int maxWidth) {
        itemName = StringUtil.capitalizeFirstLetter(itemName.replace('_', ' '), ' ');
        int width = getMinecraftStringWidth(itemName);
        if (width <= maxWidth) {
            return itemName;
        }
        String[] itemParts = itemName.split(" ");
        itemName = String.join("", itemParts);
        width = getMinecraftStringWidth(itemName);
        if (width <= maxWidth) {
            return itemName;
        }
        int exceeding = width - maxWidth;
        int shortestIndex = 0;
        int longestIndex = 0;
        for (int i = 0; i < itemParts.length; i++) {
            if (getMinecraftStringWidth(itemParts[longestIndex]) < getMinecraftStringWidth(itemParts[i])) {
                longestIndex = i;
            }
            if (getMinecraftStringWidth(itemParts[shortestIndex]) > getMinecraftStringWidth(itemParts[i])) {
                shortestIndex = i;
            }
        }
        int shortestWidth = getMinecraftStringWidth(itemParts[shortestIndex]);
        int longestWidth = getMinecraftStringWidth(itemParts[longestIndex]);
        int remove = longestWidth - shortestWidth;
        while (remove > 0 && exceeding > 0) {
            int endWidth = getMinecraftCharWidth(itemParts[longestIndex].charAt(itemParts[longestIndex].length() - 1));
            itemParts[longestIndex] = itemParts[longestIndex].substring(0, itemParts[longestIndex].length() - 1);
            remove -= endWidth;
            exceeding -= endWidth;
        }

        for (int i = itemParts.length - 1; i >= 0 && exceeding > 0; i--) {
            int partWidth = getMinecraftStringWidth(itemParts[i]);

            if (partWidth > shortestWidth) {
                remove = partWidth - shortestWidth;
            }

            if (remove > exceeding) {
                remove = exceeding;
            }

            while (remove > 0) {
                int endWidth = getMinecraftCharWidth(itemParts[i].charAt(itemParts[i].length() - 1));
                itemParts[i] = itemParts[i].substring(0, itemParts[i].length() - 1);
                remove -= endWidth;
                exceeding -= endWidth;
            }
        }

        while (exceeding > 0) {
            for (int i = itemParts.length - 1; i >= 0 && exceeding > 0; i--) {
                int endWidth = getMinecraftCharWidth(itemParts[i].charAt(itemParts[i].length() - 1));
                itemParts[i] = itemParts[i].substring(0, itemParts[i].length() - 1);
                exceeding -= endWidth;
            }
        }
        return String.join("", itemParts);
    }

    /**
     * Gives you an ItemStack from a String
     *
     * @param itemName Item name
     * @return ItemStack
     */
    public static ItemStack getItem(String itemName) {
        ItemStack itemStack = Odd.getFromString(itemName);

        if (itemStack != null) {
            return itemStack;
        }

        String[] split = itemName.split("[:\\-#]");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }

        short durability = getDurability(itemName);
        MaterialParseEvent parseEvent = new MaterialParseEvent(split[0], durability);
        Bukkit.getPluginManager().callEvent(parseEvent);
        Material material = parseEvent.getMaterial();
        if (material == null) {
            return null;
        }

        itemStack = new ItemStack(material);

        ItemMeta meta = getMetadata(itemName);

        if (meta != null) {
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(durability);
            }
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    /**
     * Returns the durability from a string
     *
     * @param itemName Item name
     * @return Durability found
     */
    public static short getDurability(String itemName) {
        Matcher m = DURABILITY.matcher(itemName);

        if (!m.find()) {
            return 0;
        }

        String data = m.group();

        if (data == null || data.isEmpty()) {
            return 0;
        }

        data = data.substring(1);

        return NumberUtil.isShort(data) ? Short.valueOf(data) : 0;
    }

    /**
     * Returns metadata from a string
     *
     * @param itemName Item name
     * @return Metadata found
     */
    public static ItemMeta getMetadata(String itemName) {
        Matcher m = METADATA.matcher(itemName);

        if (!m.find()) {
            return null;
        }

        String group = m.group().substring(1);
        return Metadata.getFromCode(group);
    }

    private static class EnumParser<E extends Enum<E>> {
        private E parse(String name, E[] values) {
            try {
                return E.valueOf(values[0].getDeclaringClass(), name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                E currentEnum = null;
                String[] typeParts = name.replaceAll("([a-z])([A-Z1-9])", "$1_$2").toUpperCase(Locale.ROOT).split("[ _]");
                int length = Short.MAX_VALUE;
                for (E e : values) {
                    String enumName = e.name();
                    if (enumName.length() < length && enumName.startsWith(name)) {
                        length = (short) enumName.length();
                        currentEnum = e;
                    } else if (typeParts.length > 1) {
                        String[] nameParts = enumName.split("_");
                        if (typeParts.length == nameParts.length) {
                            boolean matched = true;
                            for (int i = 0; i < nameParts.length; i++) {
                                if (!nameParts[i].startsWith(typeParts[i])) {
                                    matched = false;
                                    break;
                                }
                            }
                            if (matched) {
                                currentEnum = e;
                                break;
                            }
                        }
                    }
                }
                return currentEnum;
            }
        }
    }

    public static class Metadata {
        /**
         * Returns the ItemMeta represented by this code
         *
         * @param code Code representing the ItemMeta
         * @return ItemMeta represented by code
         */
        public static ItemMeta getFromCode(String code) {
            ItemStack item = ChestShop.getItemDatabase().getFromCode(code);

            if (item == null) {
                return null;
            } else {
                return item.getItemMeta();
            }
        }

        /**
         * Returns the code for this item
         *
         * @param item Item being represented
         * @return Code representing the item
         */
        public static String getItemCode(ItemStack item) {
            return ChestShop.getItemDatabase().getItemCode(item);
        }
    }

    public static class Odd {
        private static boolean isInitialized = false;

        /**
         * Returns the item stack from OddItem plugin
         *
         * @param itemName Item name to parse
         * @return itemStack that was parsed
         */
        public static ItemStack getFromString(String itemName) {
            if (!isInitialized) {
                return null;
            }

            String name = itemName.replace(':', ';');

            try {
                return OddItem.getItemStack(name);
            } catch (Exception ex) {
                return null;
            }
        }

        public static String getAlias(ItemStack itemStack) {
            if (!isInitialized) {
                return null;
            }

            try {
                Collection<String> aliases = OddItem.getAliases(itemStack);
                if (!aliases.isEmpty()) {
                    return aliases.iterator().next();
                }
            } catch (Exception ignored) {
            }
            return null;
        }

        /**
         * Lets the class know that it's safe to use the OddItem methods now
         */
        public static void initialize() {
            isInitialized = true;
        }
    }

    public static class Show {
        private static ShowItem showItem = null;

        /**
         * Lets the class know that it's safe to use the ShowItem methods now
         *
         * @param plugin
         */
        public static void initialize(Plugin plugin) {
            showItem = (ShowItem) plugin;
        }

        /**
         * Send a message with hover info and icons
         *
         * @param player  The player to send the message to
         * @param message The raw message
         * @param stock   The items in stock
         */
        public static boolean sendMessage(Player player, String message, ItemStack[] stock) {
            if (showItem == null) {
                return false;
            }

            List<String> itemJson = new ArrayList<>();
            for (ItemStack item : InventoryUtil.mergeSimilarStacks(stock)) {
                try {
                    itemJson.add(showItem.getItemConverter().createComponent(item, Level.FINE).toJsonString(player));
                } catch (Exception e) {
                    ChestShop.getPlugin().getLogger().log(Level.WARNING, "Error while trying to send message '" + message + "' to player " + player.getName() + ": " + e.getMessage());
                    return false;
                }
            }

            String joinedItemJson = itemJson.stream().collect(Collectors.joining("," + new JSONObject(ImmutableMap.of("text", " ")).toJSONString() + ", "));

            String prevColor = "";
            List<String> parts = new ArrayList<>();
            for (String s : message.split("%item")) {
                parts.add(new JSONObject(ImmutableMap.of("text", prevColor + s)).toJSONString());
                prevColor = ChatColor.getLastColors(s);
            }

            String messageJsonString = String.join("," + joinedItemJson + ",", parts);

            while (messageJsonString.startsWith(",")) {
                messageJsonString = messageJsonString.substring(1);
            }
            while (messageJsonString.endsWith(",")) {
                messageJsonString = messageJsonString.substring(0, messageJsonString.length() - 1);
            }

            showItem.tellRaw(player, messageJsonString);
            return true;
        }
    }
}
