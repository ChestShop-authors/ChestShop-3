package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.Economy;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;

/**
 * @author Acrobot
 */
public class pluginDisable extends ServerListener{
    public void onPluginDisable(PluginDisableEvent event){
        if(Economy.economy != null && pluginEnable.methods.hasMethod()){
            boolean check = pluginEnable.methods.checkDisabled(event.getPlugin());

            if(check){
                Economy.economy = null;
                System.out.println("[ChestShop] Economy plugin disabled!");
            }
        }
    }
}
