package com.nijikokun.register.payment.forChestShop;

import com.nijikokun.register.payment.forChestShop.methods.BOSE7;
import com.nijikokun.register.payment.forChestShop.methods.EE17;
import com.nijikokun.register.payment.forChestShop.methods.iCo5;
import com.nijikokun.register.payment.forChestShop.methods.iCo6;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Acrobot
 */
public class Methods {
    private static final Map<Method, String> PLUGINS_TO_LOAD = new HashMap<Method, String>() {{
        put(new iCo5(), "iConomy");
        put(new iCo6(), "iConomy");
        put(new BOSE7(), "BOSEconomy");
        put(new EE17(), "Essentials");
    }};
    private static String preferredPlugin;

    public static void setPreferred(String plugin) {
        preferredPlugin = plugin;
    }

    public static Method load() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        Method preferred = getPreferredMethod();
        if (preferred != null) {
            return preferred;
        }

        for (String pluginName : PLUGINS_TO_LOAD.values()) {
            Plugin plugin = pluginManager.getPlugin(pluginName);
            if (plugin != null) {
                Method method = createMethod(plugin);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    private static Method createMethod(Plugin plugin) {
        for (Method method : PLUGINS_TO_LOAD.keySet()) {
            if (method.isCompatible(plugin)) {
                method.setPlugin(plugin);
                return method;
            }
        }
        return null;
    }

    private static Method getPreferredMethod() {
        if (preferredPlugin.isEmpty()) {
            return null;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(preferredPlugin);

        if (plugin == null) {
            return null;
        }

        return createMethod(plugin);
    }
}
