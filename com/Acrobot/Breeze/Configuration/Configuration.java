package com.Acrobot.Breeze.Configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A class which can be used to make configs easier to load
 *
 * @author Acrobot
 */
public class Configuration {
    /**
     * Loads a YAML-formatted file into a class and modifies the file if some of class's fields are missing
     *
     * @param file  File to load
     * @param clazz Class to modify
     */
    public static void pairFileAndClass(File file, Class clazz) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())) {
                    continue;
                }

                String path = field.getName();

                try {
                    if (path.toLowerCase().replace("_", "").startsWith("newline")) {
                        continue;
                    }

                    if (config.isSet(path)) {
                        field.set(null, ValueParser.parseToJava(config.get(path)));
                    } else {
                        writer.write('\n' + FieldParser.parse(field));

                        if (clazz.getDeclaredField("NEWLINE_" + path) != null) {
                            writer.write('\n');
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    continue;
                }
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a java value to config-compatible value
     *
     * @param value Value to parse
     * @return Parsed output
     */
    public static String parseToConfig(Object value) {
        return ValueParser.parseToYAML(value);
    }

    /**
     * Colourises a string (using '&' character)
     *
     * @param string String to colourise
     * @return Colourised string
     */
    public static String getColoured(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
