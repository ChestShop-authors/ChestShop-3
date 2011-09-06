package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Economy;
import com.LRFLEW.register.payment.forChestShop.Methods;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;

/**
 * @author Acrobot
 */
public class pluginDisable extends ServerListener {
    public void onPluginDisable(PluginDisableEvent event) {
        if (Economy.economy != null && Methods.hasMethod() && Methods.checkDisabled(event.getPlugin())) {
            Economy.economy = null;
            System.out.println("[ChestShop] Economy plugin disabled!");
        }
    }
}
