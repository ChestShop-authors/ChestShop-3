package com.Acrobot.Breeze.Configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Acrobot
 */
public class ValueParser {
    /**
     * Parses an object to a YAML-usable string
     *
     * @param object Object to parse
     * @return YAML string
     */
    public static String parseToYAML(Object object) {
        if (object instanceof Number || object instanceof Boolean) {
            return String.valueOf(object);
        } else {
            return '\"' + String.valueOf(object) + '\"';
        }
    }

    /**
     * Parses a YAML "object" to Java-compatible object
     *
     * @param object Object to parse
     * @return Java-compatible object
     */
    public static Object parseToJava(Object object) {
        if (object instanceof ConfigurationSection) {
            Map<String, List<String>> map = new HashMap<String, List<String>>();

            for (String message : ((ConfigurationSection) object).getKeys(false)) {
                map.put(message, ((ConfigurationSection) object).getStringList(message));
            }

            return map;
        } else {
            return object;
        }
    }
}
