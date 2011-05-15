package com.Acrobot.iConomyChestShop.Listeners;

import com.Acrobot.iConomyChestShop.Economy;
import com.Acrobot.iConomyChestShop.Items.Odd;
import com.Acrobot.iConomyChestShop.Permission;
import com.Acrobot.iConomyChestShop.Protection.LWCplugin;
import com.Acrobot.iConomyChestShop.Protection.LockettePlugin;
import com.Acrobot.iConomyChestShop.Protection.Security;
import com.Acrobot.iConomyChestShop.iConomyChestShop;
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

    private Methods Methods = new Methods();
    public void onPluginEnable(PluginEnableEvent event){

        if(!this.Methods.hasMethod()){
            if(this.Methods.setMethod(event.getPlugin())){
                Economy.economy = this.Methods.getMethod();
                System.out.println("[iConomyChestShop] " + Economy.economy.getName() + " " + Economy.economy.getVersion() + " loaded.");
            }
        }

        //Permissions
        if (Permission.permissions == null) {
            Plugin permissions = iConomyChestShop.getBukkitServer().getPluginManager().getPlugin("Permissions");

            if (permissions != null) {
                Permission.permissions = ((Permissions) permissions).getHandler();
                PluginDescriptionFile pDesc = permissions.getDescription();
                System.out.println("[iConomyChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }

        //LWC
        if (LWCplugin.lwc == null) {
            Plugin lwcPlugin = iConomyChestShop.getBukkitServer().getPluginManager().getPlugin("LWC");

            if (lwcPlugin != null) {
                PluginDescriptionFile pDesc = lwcPlugin.getDescription();
                LWCplugin.lwc = ((LWCPlugin) lwcPlugin).getLWC();
                Security.protection = new LWCplugin();
                System.out.println("[iConomyChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }

        //OddItem
        if (Odd.oddItem == null) {
            Plugin oddItem = iConomyChestShop.getBukkitServer().getPluginManager().getPlugin("OddItem");

            if (oddItem != null) {
                PluginDescriptionFile pDesc = oddItem.getDescription();
                Odd.oddItem = (OddItem) iConomyChestShop.getBukkitServer().getPluginManager().getPlugin("OddItem");
                System.out.println("[iConomyChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }

        //Lockette
        if (LockettePlugin.lockette == null) {
            Plugin lockette = iConomyChestShop.getBukkitServer().getPluginManager().getPlugin("Lockette");

            if (lockette != null) {
                PluginDescriptionFile pDesc = lockette.getDescription();
                LockettePlugin.lockette = ((Lockette) lockette);
                Security.protection = new LockettePlugin();
                System.out.println("[iConomyChestShop] " + pDesc.getName() + " version " + pDesc.getVersion() + " loaded.");
            }
        }
    }
}
