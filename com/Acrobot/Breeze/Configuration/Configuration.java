package com.Acrobot.Breeze.Configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Acrobot
 */
public class Configuration {
    public static void loadFileIntoClass(File file, Class clazz) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                String path = field.getName();

                try {
                    if (config.isSet(path)) {
                        field.set(null, config.get(path));
                    } else {
                        configureProperty(bw, field);
                    }
                } catch (IllegalAccessException ignored) {
                }
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void configureProperty(BufferedWriter writer, Field field) {
        try {
            writer.write('\n' + field.getName() + ": " + retrieveValue(field.get(null)));

            if (field.isAnnotationPresent(ConfigurationComment.class)) {
                writer.write('\n' + "#" + field.getAnnotation(ConfigurationComment.class).value());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String retrieveValue(Object value) {
        StringBuilder toReturn = new StringBuilder(30);

        if (value instanceof Number || value instanceof Boolean) {
            toReturn.append(String.valueOf(value));
        } else {
            toReturn.append('\"').append(String.valueOf(value)).append('\"');
        }

        return toReturn.toString();
    }
}
