package com.Acrobot.ChestShop.Config;

/**
 * @author Acrobot
 */
public class Value {
    public final Object value;
    public final String comment;

    public Value(Object value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public Value(Object value) {
        this(value, null);
    }

    /**
     * Retrieves the value of that Value
     *
     * @return The value
     */
    public String retrieveValue() {
        StringBuilder toReturn = new StringBuilder(30);

        if (value instanceof Number || value instanceof Boolean) {
            toReturn.append(String.valueOf(value));
        } else {
            toReturn.append('\"').append(String.valueOf(value)).append('\"');
        }

        if (comment != null) {
            toReturn.append('\n').append('#').append(comment);
        }

        return toReturn.toString();
    }
}
