package com.nijikokun.register.payment.forChestShop;

import com.nijikokun.register.payment.forChestShop.methods.BOSE7;
import com.nijikokun.register.payment.forChestShop.methods.EE17;
import com.nijikokun.register.payment.forChestShop.methods.iCo5;
import com.nijikokun.register.payment.forChestShop.methods.iCo6;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * @author Acrobot
 */
public class Methods {
    private static String preferred;
    private static final String[] toLoad = new String[]{
            "iConomy",
            "BOSEconomy",
            "Essentials",
    };
    private static final Method[] methods = new Method[]{
            new iCo5(),
            new iCo6(),
            new BOSE7(),
            new EE17()
    };

    public static void setPreferred(String plugin) {
        preferred = plugin;
    }

    public static Method load(PluginManager pm) {
        if (!preferred.isEmpty()){
            Plugin plugin = pm.getPlugin(preferred);
            if (plugin != null){
                Method m = createMethod(plugin);
                if (m != null) return m;
            }
        }

        for (String plugin : toLoad){
            Plugin pl = pm.getPlugin(plugin);
            if (pl != null){
                Method m = createMethod(pl);
                if (m != null) return m;
            }
        }
        return null;
    }

    public static Method createMethod(Plugin plugin) {
        for (Method method : methods){
            if (method.isCompatible(plugin)) {
                method.setPlugin(plugin);
                return method;
            }
        }
        return null;
    }
}
