package com.Acrobot.Breeze.Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Acrobot.Breeze.Configuration.Annotations.Parser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Acrobot.Breeze.Configuration.Annotations.PrecededBySpace;

/**
 * A class which can be used to make configs easier to load
 *
 * @author Acrobot
 */
public class Configuration {
    private static Map<String, ValueParser> parsers = new HashMap<>();
    private static final ValueParser DEFAULT_PARSER = new ValueParser();
    private static final ValueParser ENUM_PARSER = new ValueParser() {
        @Override
        public <T> Object parseToJava(Class<T> type, Object object) {
            if (object instanceof String && type.isEnum()) {
                return Enum.valueOf((Class<? extends Enum>) type, ((String) object).toUpperCase(Locale.ROOT));
            }
            return object;
        }
    };

    /**
     * Loads a YAML-formatted file into a class and modifies the file if some of class's fields are missing
     *
     * @param file  File to load
     * @param clazz Class to modify
     */
    public static void pairFileAndClass(File file, Class<?> clazz) {
        pairFileAndClass(file, clazz, Bukkit.getLogger());
    }

    /**
     * Loads a YAML-formatted file into a class and modifies the file if some of class's fields are missing
     *
     * @param file      File to load
     * @param clazz     Class to modify
     * @param logger    The logger to use to log some information about the pairing
     */
    public static void pairFileAndClass(File file, Class<?> clazz, Logger logger) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean debug = config.getBoolean("DEBUG", false);

        if (debug) {
            logger.log(Level.INFO, "Loading configuration " + file.getName());
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            if (!endsWithSpace(file)) {
                writer.newLine();
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())) {
                    if (debug) {
                        logger.log(Level.WARNING, "Field " + field.getName() + " is private, transient or not static!");
                    }
                    continue;
                }

                String path = field.getName();

                try {
                    if (config.isSet(path)) {
                        field.set(null, getParser(field).parseToJava(field.getType(), config.get(path)));
                    } else if (config.isSet(path.toLowerCase(Locale.ROOT))) {
                        field.set(null, getParser(field).parseToJava(field.getType(), config.get(path.toLowerCase(Locale.ROOT))));
                    } else {
                        if (field.isAnnotationPresent(PrecededBySpace.class)) {
                            writer.newLine();
                        }

                        writer.write(FieldParser.parse(field));
                        writer.newLine();
                    }
                    if (debug) {
                        logger.log(Level.INFO, field.getName() + ": " + Configuration.getParser(field).parseToYAML(field.get(null)));
                    }
                } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
                    logger.log(Level.SEVERE, "Error while loading field " + field.getName() + " in configuration " + file.getName(), e);
                }
            }

            writer.close();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading configuration " + file.getName(), e);
        }
    }

    /**
     * Checks if the file ends with space
     *
     * @param file File to check
     * @return If the file ends with space
     */
    public static boolean endsWithSpace(File file) {
        try (Scanner scanner = new Scanner(file)) {
            String lastLine = "";

            while (scanner.hasNextLine()) {
                lastLine = scanner.nextLine();
            }

            return lastLine.isEmpty();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts a java value to config-compatible value
     *
     * @param value Value to parse
     * @return Parsed output
     */
    public static String parseToConfig(Object value) {
        return DEFAULT_PARSER.parseToYAML(value);
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

    /**
     * Register a parser
     * @param name The name of the parser
     * @param valueParser The parser itself
     */
    public static void registerParser(String name, ValueParser valueParser) {
        parsers.put(name.toLowerCase(Locale.ROOT), valueParser);
    }

    /**
     * Get a registered parser
     * @param name The name of the parser
     * @return The parser or null if it doesn't exist
     */
    public static ValueParser getParser(String name) {
        return parsers.get(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Get the parser that should be used for a field
     * @param field The field
     * @return The registered parser if the field has a Parser annotation or the default one
     */
    public static ValueParser getParser(Field field) {
        ValueParser parser = null;
        if (field.isAnnotationPresent(Parser.class)) {
            parser = Configuration.getParser(field.getAnnotation(Parser.class).value());
        }
        if (parser == null) {
            parser = Configuration.getParser(field.getType().getSimpleName());
        }
        if (parser == null && field.getType().isEnum()) {
            parser = Configuration.ENUM_PARSER;
        }
        if (parser == null) {
            parser = Configuration.DEFAULT_PARSER;
        }
        return parser;
    }
}
