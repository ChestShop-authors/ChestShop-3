package com.Acrobot.ChestShop.Options;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public enum Option {
    BALANCE("balance", true),
    OUT_OF_STOCK("outOfStock", true),
    SOMEONE_BOUGHT("someoneBought", true);

    private boolean enabled;
    private String name;
    private static final Map<String, Option> names = new HashMap<String, Option>();

    private Option(String name, boolean enabled) {
        this.enabled = enabled;
        this.name = name;
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
