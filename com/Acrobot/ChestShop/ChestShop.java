package com.Acrobot.ChestShop;

import com.Acrobot.ChestShop.Items.Items;
import com.Acrobot.ChestShop.Listeners.*;
import com.Acrobot.ChestShop.Utils.Config;
import com.Acrobot.ChestShop.Utils.Defaults;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main file of the plugin
 * @author Acrobot
 */
public class ChestShop extends JavaPlugin {

    private final pluginEnable pluginEnable = new pluginEnable();
    private final blockBreak blockBreak = new blockBreak();
    private final blockPlace blockPlace = new blockPlace();
    private final signChange signChange = new signChange();
    private final playerInteract playerInteract = new playerInteract();

    private PluginDescriptionFile desc;
    private static Server server;

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.BLOCK_BREAK, blockBreak, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockPlace, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, signChange, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Highest, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginEnable, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerInteract, Event.Priority.Monitor, this);

        desc = this.getDescription();
        server = getServer();

        Config.setUp();
        Defaults.set();

        System.out.println('[' + desc.getName() + "] version " + desc.getVersion() + " initialized!");
    }

    public void onDisable() {
        System.out.println('[' + desc.getName() + "] version " + desc.getVersion() + " shutting down!");
    }

    public static Server getBukkitServer() {
        return server;
    }

    public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args){
        String commandName = cmd.getName().toLowerCase();
        int argCount = args.length;

        //iCSversion
        if(commandName.equals("icsversion")){
            sender.sendMessage("ChestShop's version is: " + desc.getVersion());
            return true;
        }

        if(!(sender instanceof Player)){
            return false;
        }
        Player p = (Player) sender;

        //ItemInfo
        if(commandName.equals("iteminfo")){
            if(argCount == 0){
                p.sendMessage(Items.getItemID(p.getItemInHand().getType().name()) + " " + Items.getItemName(p.getItemInHand()));
                return true;
            }
            if(argCount == 1){
                String itemName = Items.getItemID(Items.getItemName(args[0])) + " " + Items.getItemName(args[0]);
                p.sendMessage(itemName);
                return true;
            }
        }

        //Silly :)
        if(commandName.equals("buy")){
            p.sendMessage("Hey, there is no buy command! Just right click the sign!");
            return true;
        }
        if(commandName.equals("sell")){
            p.sendMessage("Hey, there is no sell command! Just left click the sign!");
            return true;
        }
        
        return false;
    }
}
