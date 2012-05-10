package com.Acrobot.ChestShop.Config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public class BreezeConfiguration extends YamlConfiguration {
    protected final File file;
    protected final Map<String, Value> defaultValues = new LinkedHashMap<String, Value>();

    protected BreezeConfiguration(File file) {
        this.file = file;
    }

    /**
     * Adds default values for the config
     *
     * @param map The default values to add
     */
    public void addDefaultValues(Map<String, ? extends Value> map) {
        defaultValues.putAll(map);
    }

    /**
     * Creates a new BreezeConfiguration object
     *
     * @param file file to load config from
     * @return BreezeConfiguration object
     */
    public static BreezeConfiguration loadConfiguration(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        BreezeConfiguration config = new BreezeConfiguration(file);

        config.load();

        return config;
    }

    /**
     * Creates a new BreezeConfiguration object
     *
     * @param file     file to load config from
     * @param defaults default values
     * @return BreezeConfiguration object
     */
    public static BreezeConfiguration loadConfiguration(File file, Map<String, Value> defaults) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        BreezeConfiguration config = new BreezeConfiguration(file);

        config.addDefaultValues(defaults);

        config.load();

        return config;
    }

    /**
     * Loads the config
     */
    public void load() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            super.load(file);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (createDefaultValues()) {
            load();
        }
    }

    /**
     * Reloads (saves and loads) the config
     */
    public void reload() {
        save();
        load();
    }

    /**
     * Creates default values
     *
     * @return were any values added?
     */
    private boolean createDefaultValues() {
        boolean changed = false;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            for (Map.Entry<String, Value> entry : defaultValues.entrySet()) {
                if (this.contains(entry.getKey())) {
                    continue;
                }

                changed = true;
                bw.write('\n' + entry.getKey() + ": " + entry.getValue().retrieveValue());
            }

            bw.close();

            if (changed) {
                load();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return changed;
    }

    /**
     * Saves the config
     */
    public void save() {
        try {
            super.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
