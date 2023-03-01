package com.Acrobot.Breeze.Configuration;

import com.Acrobot.Breeze.Configuration.Annotations.ConfigurationComment;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Acrobot
 */
public class FieldParser {
    /**
     * Parses a field into a YAML-compatible string
     *
     * @param field Field to parse
     * @return Parsed field
     */
    public static String parse(Field field) {
        StringBuilder builder = new StringBuilder(50);

        if (field.isAnnotationPresent(ConfigurationComment.class)) {
            for (String commentLine : field.getAnnotation(ConfigurationComment.class).value().split("\n")) {
                builder.append("# ").append(commentLine).append('\n');
            }
        }

        ValueParser parser = Configuration.getParser(field);

        try {
            builder.append(field.getName()).append(": ").append(parser.parseToYAML(field.get(null)));
        } catch (IllegalAccessException e) {
            Logger.getLogger("FieldParser").log(Level.SEVERE, "Error while parsing field", e);
            return "";
        }

        return builder.toString();
    }
}
