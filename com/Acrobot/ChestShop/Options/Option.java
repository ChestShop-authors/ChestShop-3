package com.Acrobot.ChestShop.Options;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public enum Option {
    BALANCE(true),
    OUT_OF_STOCK(true),
    SOMEONE_BOUGHT(true);

    private boolean enabled;
    private static final Map<String, Option> names = new HashMap<String, Option>();

    private Option(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static Option getOption(String name) {
        return names.get(name);
    }

    static {
        for (Option op : values()) {
            names.put(op.name(), op);
        }
    }
}
