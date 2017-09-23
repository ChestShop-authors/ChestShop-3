package com.Acrobot.Breeze.Database;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parses an entity (class with database fields)
 *
 * @author Acrobot
 */
public class EntityParser {
    private Class<?> entity;

    public EntityParser(Class<?> table) {
        if (!table.isAnnotationPresent(Entity.class) || !table.isAnnotationPresent(javax.persistence.Table.class)) {
            throw new AnnotationFormatError("The class hasn't got Entity or Table annotation!");
        }

        entity = table;
    }

    /**
     * Parses the class' fields to a standard SQL format
     *
     * @return SQLed class
     */
    public String parseToString() {
        List<String> fields = new LinkedList<String>();

        for (Field field : entity.getDeclaredFields()) {
            fields.add(convertToSQL(field));
        }

        return fields.stream().collect(Collectors.joining(","));
    }

    /**
     * Converts a field type to SQL type
     *
     * @param field Java's field
     * @return SQL type
     */
    public static String convertToSQL(Field field) {
        String sqlType = field.getName();
        Class<?> type = field.getType();

        if (type.isAssignableFrom(boolean.class)) {
            sqlType += " BOOLEAN";
        } else if (type.isAssignableFrom(int.class)) {
            sqlType += " INTEGER";
        } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(float.class)) {
            sqlType += " REAL";
        } else {
            sqlType += " TEXT";
        }

        if (field.isAnnotationPresent(Id.class)) {
            sqlType += " PRIMARY KEY";
        }

        return sqlType;
    }
}
