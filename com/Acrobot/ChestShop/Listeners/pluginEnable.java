package com.Acrobot.ChestShop.Listeners;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Economy;
import com.Acrobot.ChestShop.Items.Odd;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Protection.LWCplugin;
import com.Acrobot.ChestShop.Protection.LockettePlugin;
import com.Acrobot.ChestShop.Protection.Security;
import com.griefcraft.lwc.LWCPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.Methods;
import info.somethingodd.bukkit.odd.item.OddItem;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.yi.acru.bukkit.Lockette.Lockette;

/**
 * @author Acrobot
 */
public class pluginEnable extends ServerListener{

    private Methods methods = new Methods();

    
    public void onPluginEnable(PluginEnableEvent event){
        
        //Economy plugins
        if(!this.methods.hasMethod()){
            if(methods.setMethod(event.getPlugin())){
                Economy.economy = methods.getMethod();
                System.out.println("[ChestShop] " + Economy.economy.getName() + " " + Economy.economy.getVersion() + " loaded.");
            }
        }

        //Permissions
        if (Permission.permissions == null) {
            Plugin permissions = ChestShop.getBukkitServer().getPluginManager().getPlugin("Permissions");

            if (permissions != null) {
                Permission.permissions = ((Permissions) permissions).getHandler();
                PluginDescriptionFile pDesc = permissions.getDescription();
                System.out.println("[ChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }

        //LWC
        if (LWCplugin.lwc == null) {
            Plugin lwcPlugin = ChestShop.getBukkitServer().getPluginManager().getPlugin("LWC");

            if (lwcPlugin != null) {
                PluginDescriptionFile pDesc = lwcPlugin.getDescription();
                LWCplugin.lwc = ((LWCPlugin) lwcPlugin).getLWC();
                Security.protection = new LWCplugin();
                System.out.println("[ChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }

        //OddItem
        if (Odd.oddItem == null) {
            Plugin oddItem = ChestShop.getBukkitServer().getPluginManager().getPlugin("OddItem");

            if (oddItem != null) {
                PluginDescriptionFile pDesc = oddItem.getDescription();
                Odd.oddItem = (OddItem) ChestShop.getBukkitServer().getPluginManager().getPlugin("OddItem");
                System.out.println("[ChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }

        //Lockette
        if (LockettePlugin.lockette == null) {
            Plugin lockette = ChestShop.getBukkitServer().getPluginManager().getPlugin("Lockette");

            if (lockette != null) {
                PluginDescriptionFile pDesc = lockette.getDescription();
                LockettePlugin.lockette = ((Lockette) lockette);
                Security.protection = new LockettePlugin();
                System.out.println("[ChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }
    }
}
