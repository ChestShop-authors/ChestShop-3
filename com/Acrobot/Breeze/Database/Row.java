package com.Acrobot.Breeze.Database;

import java.util.*;

/**
 * A class representing a Row in SQL query
 *
 * @author Acrobot
 */
public class Row {
    private List<String> keys = new ArrayList<String>();
    private Map<String, String> values = new HashMap<String, String>();

    /**
     * Puts a name/value pair inside the row
     *
     * @param name  Item's name
     * @param value Item's value
     */
    public void put(String name, String value) {
        if (!values.containsKey(name)) {
            keys.add(name);
        }

        values.put(name, value);
    }

    /**
     * Gets a value from name
     *
     * @param name Value's name
     * @return Value
     */
    public String get(String name) {
        return values.get(name);
    }

    /**
     * Gets a value from index
     *
     * @param index Value's index
     * @return Value
     */
    public String get(int index) {
        String key = keys.get(index);
        return values.get(key);
    }

    /**
     * Returns value's key
     *
     * @param index Value's index
     * @return Value's key
     */
    public String getKey(int index) {
        return keys.get(index);
    }

    /**
     * Returns all values
     *
     * @return All values in this row
     */
    public Collection<String> getValues() {
        return values.values();
    }

    /**
     * Returns a string of all values separated by a comma
     *
     * @return String
     */
    public String stringOfValues() {
        StringBuilder builder = new StringBuilder(30);

        for (int i = 0; i < values.size(); i++) {
            builder.append('\'').append(get(i)).append('\'');

            if (i != (values.size() - 1)) {
                builder.append(',');
            }
        }

        return builder.toString();
    }

    /**
     * Returns the number of values inside the row
     *
     * @return number of values
     */
    public int getSize() {
        return values.size();
    }
}