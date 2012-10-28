package com.Acrobot.Breeze.Database;

import com.google.common.base.Joiner;

import javax.persistence.Entity;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Parses an entity (class with database fields)
 *
 * @author Acrobot
 */
public class EntityParser {
    private Class entity;

    public EntityParser(Class table) {
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

        return Joiner.on(',').join(fields);
    }

    /**
     * Converts a field type to SQL type
     *
     * @param field Java's field
     * @return SQL type
     */
    public String convertToSQL(Field field) {
        return null;
    }
}
