package com.Acrobot.Breeze.Utils;

import com.Acrobot.Breeze.Collection.SimpleCache;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.MaterialParseEvent;
import com.Acrobot.ChestShop.Utils.ItemUtil;
import de.themoep.ShowItem.api.ShowItem;
import de.themoep.minedown.adventure.Replacer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Map<String, String> ABBREVIATIONS = StringUtil.map(
            "Egg", "Eg",
            "Spawn", "Spaw",
            "Pottery", "Pot",
            "Heartbreak", "Heartbr",
            "Sherd", "Sher"
    );

    private static final Map<String, String> UNIDIRECTIONAL_ABBREVIATIONS = StringUtil.map(
            "Endermite", "Endmite",
            "Endmite", "Endmit",
            "Wayfinder", "Wayfndr",
            "Wayfndr", "Wf",
            "Heartbr", "Hrtbr",
            "Hrtbr", "Hrtb"
    );

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
        ItemMeta oneMeta = one.getItemMeta();
        ItemMeta twoMeta = two.getItemMeta();
        // return true if both are null or same, false if only one is null
        if (oneMeta == twoMeta || oneMeta == null || twoMeta == null) {
            return oneMeta == twoMeta;
        }
        Map<String, Object> oneSerMeta = oneMeta.serialize();
        Map<String, Object> twoSerMeta = twoMeta.serialize();
        if (oneSerMeta.equals(twoSerMeta)) {
            return true;
        }

        // Try to use same parsing as the YAML dumper in the ItemDatabase when generating the code as the last resort
        ItemStack oneDumped = YAML.loadAs(YAML.dump(one), ItemStack.class);
        if (oneDumped.isSimilar(two)) {
            return true;
        }

        ItemMeta oneDumpedMeta = oneDumped.getItemMeta();
        if (oneDumpedMeta != null && oneDumpedMeta.serialize().equals(twoSerMeta)) {
            return true;
        }

        ItemStack twoDumped = YAML.loadAs(YAML.dump(two), ItemStack.class);
        if (oneDumped.isSimilar(twoDumped)) {
            return true;
        }

        ItemMeta twoDumpedMeta = twoDumped.getItemMeta();
        if (oneDumpedMeta != null && twoDumpedMeta != null && oneDumpedMeta.serialize().equals(twoDumpedMeta.serialize())) {
            return true;
        }

        // return true if both are null or same, false otherwise
        return oneDumpedMeta == twoDumpedMeta;
    }

    /**
     * Gives you a Material from a String (doesn't have to be fully typed in)
     *
     * @param name Name of the material
     * @return Material found
     */
    public static Material getMaterial(String name) {
        String replacedName = name;
        // revert unidirectional abbreviations
        List<Map.Entry<String, String>> abbreviations = new ArrayList<>(UNIDIRECTIONAL_ABBREVIATIONS.entrySet());
        for (int i = abbreviations.size() - 1; i >= 0; i--) {
            Map.Entry<String, String> entry = abbreviations.get(i);
            replacedName = replacedName.replaceAll(entry.getValue() + "(_|$)?", entry.getKey() + "$1");
        }

        String formatted = name.replaceAll("(?<!^)(?>\\s?)([A-Z1-9])", "_$1").replace(' ', '_').toUpperCase(Locale.ROOT);

        Material material = MATERIAL_CACHE.get(formatted);
        if (material != null) {
            return material;
        }

        material = Material.matchMaterial(name);

        if (material != null) {
            MATERIAL_CACHE.put(formatted, material);
            return material;
        }

        material = new EnumParser<Material>().parse(replacedName, Material.values());
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
     * @deprecated Use {@link ItemUtil#getItemList(ItemStack[])} instead!
     */
    @Deprecated
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
     * Use {@link ItemUtil#getName(ItemStack, int)} if you want to get name aliases too!
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
     * Use {@link ItemUtil#getSignName(ItemStack)} if you want to get name aliases too!
     *
     * @param itemStack ItemStack to name
     * @return ItemStack's name
     */
    public static String getSignName(ItemStack itemStack) {
        return getName(itemStack, MAXIMUM_SIGN_WIDTH);
    }

    /**
     * Returns item's name, with a maximum width.
     * Use {@link ItemUtil#getName(ItemStack, int)} if you want to get name aliases too!
     *
     * @param itemStack ItemStack to name
     * @param maxWidth The max width that the name should have; 0 or below if it should be unlimited
     * @return ItemStack's name
     */
    public static String getName(ItemStack itemStack, int maxWidth) {
        String itemName = itemStack.getType().toString();

        String durability = "";
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            if (damageable.hasDamage()) {
                durability = ":" + damageable.getDamage();
            }
        }

        String metaData = "";
        if (hasCustomData(itemStack)) {
            metaData = "#" + Metadata.getItemCode(itemStack);
        }

        String code = StringUtil.capitalizeFirstLetter(itemName, '_');
        if (maxWidth > 0) {
            int codeWidth = getMinecraftStringWidth(code + durability + metaData);
            if (codeWidth > maxWidth) {
                int exceeding = codeWidth - maxWidth;
                code = getShortenedName(code, getMinecraftStringWidth(code) - exceeding);
            }
        }

        return code + durability + metaData;
    }

    /**
     * Check whether the provided ItemStack has custom data (in the past called "ItemMeta"). This will ignore
     * the durability of an item.
     *
     * @param itemStack The ItemStack to check
     * @return Whether the item has custom data
     */
    private static boolean hasCustomData(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            if (!((Damageable) itemMeta).hasDamage()) {
                return true;
            }
        }
        Map<String, Object> data = itemMeta.serialize();
        // if the data map contains more than the metadata type and the damage
        // then the item does indeed have custom data set
        return data.size() > 2;
    }

    /**
     * Get an item name shortened to a max length that is still reversable by {@link #getMaterial(String)}
     *
     * @param itemName  The name of the item
     * @param maxWidth  The max width
     * @return The name shortened to the max length
     */
    public static String getShortenedName(String itemName, int maxWidth) {
        // Restore spaces in string that might be already be shortened
        String name = itemName.replaceAll("([a-z])([A-Z1-9])", "$1 $2");
        name = StringUtil.capitalizeFirstLetter(name.replace('_', ' '), ' ');
        int width = getMinecraftStringWidth(name);
        if (width <= maxWidth) {
            return name;
        }
        String[] itemParts = name.split("[ \\-]");
        String noSpaceName = String.join("", itemParts);
        width = getMinecraftStringWidth(noSpaceName);
        if (width <= maxWidth) {
            return noSpaceName;
        }

        // Abbreviate some terms manually
        for (Map.Entry<String, String> entry : ABBREVIATIONS.entrySet()) {
            name = name.replaceAll(entry.getKey() + "( |$)", entry.getValue() + "$1");
            itemParts = name.split("[ \\-]");
            noSpaceName = String.join("", itemParts);
            width = getMinecraftStringWidth(noSpaceName);
            if (width <= maxWidth) {
                return noSpaceName;
            }
        }

        // Apply unidirectional abbreviations if it still doesn't work
        for (Map.Entry<String, String> entry : UNIDIRECTIONAL_ABBREVIATIONS.entrySet()) {
            name = name.replaceAll(entry.getKey() + "( |$)", entry.getValue() + "$1");
            itemParts = name.split("[ \\-]");
            noSpaceName = String.join("", itemParts);
            width = getMinecraftStringWidth(noSpaceName);
            if (width <= maxWidth) {
                return noSpaceName;
            }
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
        String[] split = itemName.split("[:\\-#]");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }

        Integer durability = getDurability(itemName);
        MaterialParseEvent parseEvent = new MaterialParseEvent(split[0], durability != null ? durability.shortValue() : 0);
        Bukkit.getPluginManager().callEvent(parseEvent);
        Material material = parseEvent.getMaterial();
        if (material == null) {
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        ItemMeta meta = getMetadata(itemName);

        if (durability != null) {
            if (meta == null) {
                meta = itemStack.getItemMeta();
            }
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(durability);
            }
        }

        if (meta != null) {
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
    public static Integer getDurability(String itemName) {
        Matcher m = DURABILITY.matcher(itemName);

        if (!m.find()) {
            return null;
        }

        String data = m.group();

        if (data == null || data.isEmpty()) {
            return null;
        }

        data = data.substring(1);

        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            return 0;
        }
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
            String formatted = name.replaceAll("(?<!^)(?>\\s?)([A-Z1-9])", "_$1").toUpperCase(Locale.ROOT).replace(' ', '_');
            try {
                return E.valueOf(values[0].getDeclaringClass(), formatted);
            } catch (IllegalArgumentException exception) {
                List<E> possibleEnums = new ArrayList<>();
                String[] typeParts = formatted.split("_");
                int length = Short.MAX_VALUE;
                for (E e : values) {
                    String enumName = e.name();
                    if (enumName.length() < length && enumName.startsWith(formatted)) {
                        length = enumName.length();
                        possibleEnums.add(e);
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
                                possibleEnums.add(e);
                            }
                        }
                    }
                }

                if (possibleEnums.size() == 1) {
                    return possibleEnums.get(0);
                } else if (possibleEnums.size() > 1) {
                    int formattedLength = formatted.length();
                    int closestDeviation = Short.MAX_VALUE;
                    E closestEnum = null;
                    for (E possibleEnum : possibleEnums) {
                        int deviation = possibleEnum.name().length() - formattedLength;
                        if (deviation < closestDeviation) {
                            closestDeviation = deviation;
                            closestEnum = possibleEnum;
                        }
                    }
                    return closestEnum;
                }
                return null;
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
        public static boolean sendMessage(Player player, Messages.Message message, ItemStack[] stock, Map<String, String> replacementMap, String... replacements) {
            return sendMessage(player, player.getName(), message, stock, replacementMap, replacements);
        }

        /**
         * Send a message with hover info and icons
         *
         * @param player        The player to send the message to
         * @param playerName    The name of the player in case he is offline and bungee messages are enabled
         * @param message       The raw message
         * @param stock         The items in stock
         */
        public static boolean sendMessage(Player player, String playerName, Messages.Message message, ItemStack[] stock, Map<String, String> replacementMap, String... replacements) {
            return sendMessage(player, playerName, message, true, stock, replacementMap, replacements);
        }

        /**
         * Send a message with hover info and icons
         *
         * @param player        The player to send the message to
         * @param playerName    The name of the player in case he is offline and bungee messages are enabled
         * @param message       The raw message
         * @param showPrefix    If the prefix should show
         * @param stock         The items in stock
         */
        public static boolean sendMessage(Player player, String playerName, Messages.Message message, boolean showPrefix, ItemStack[] stock, Map<String, String> replacementMap, String... replacements) {
            if (showItem == null) {
                return false;
            }

            TextComponent.Builder itemComponent = Component.text();
            for (ItemStack item : InventoryUtil.mergeSimilarStacks(stock)) {
                try {
                    itemComponent.append(GsonComponentSerializer.gson().deserialize(showItem.getItemConverter().createComponent(item, Level.FINE).toJsonString(player)));
                } catch (Exception e) {
                    ChestShop.getPlugin().getLogger().log(Level.WARNING, "Error while trying to send message '" + message + "' to player " + player.getName() + ": " + e.getMessage());
                    return false;
                }
            }

            Map<String, String> newMap = new LinkedHashMap<>(replacementMap);
            newMap.put("material", "item");
            newMap.remove("item");
            Component component = new Replacer()
                    .placeholderSuffix("")
                    .replace("item",itemComponent.build())
                    .replaceIn(message.getComponent(player, showPrefix, newMap, replacements));
            if (player != null) {
                ChestShop.getAudiences().player(player).sendMessage(component);
                return true;
            } else if (playerName != null) {
                ChestShop.sendBungeeMessage(playerName, component);
                return true;
            }

            return true;
        }
    }
}
