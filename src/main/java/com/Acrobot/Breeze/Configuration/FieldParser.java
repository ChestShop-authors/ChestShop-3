package com.Acrobot.Breeze.Configuration;

import com.Acrobot.Breeze.Configuration.Annotations.ConfigurationComment;
import com.Acrobot.Breeze.Configuration.Annotations.Parser;

import java.lang.reflect.Field;

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
            e.printStackTrace();
            return "";
        }

        return builder.toString();
    }
}
